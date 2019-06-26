package cn.hikyson.methodcanary.lib;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Keep;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Keep
public class MethodCanaryInject {
    private static volatile boolean sStopped = true;
    private static long sStartTimeNanos;
    private static long sStopTimeNanos;
    private static AtomicInteger sTaskRunningCount = new AtomicInteger(0);
    private static int sMethodEventOfMapCount = 0;
    private static Map<ThreadInfo, List<MethodEvent>> sMethodEventMap = new HashMap<>();
    private static Handler sWorkHandler;
    private static MethodCanaryConfig sMethodCanaryConfig;

    /**
     * install sdk
     *
     * @param methodCanaryConfig
     */
    public static synchronized void install(MethodCanaryConfig methodCanaryConfig) {
        sMethodCanaryConfig = methodCanaryConfig;
        clearRuntime();
        HandlerThread worker = new HandlerThread("method-canary-record");
        worker.start();
        sWorkHandler = new Handler(worker.getLooper());
    }

    /**
     * uninstall
     */
    public static synchronized void uninstall() {
        clearRuntime();
        if (sWorkHandler != null) {
            sWorkHandler.getLooper().quit();
            sWorkHandler = null;
        }
    }

    @Keep
    public static void onMethodEnter(int accessFlag, String className, String methodName, String desc) {
        if (sStopped) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        final ThreadInfo threadInfo = new ThreadInfo(currentThread.getId(), currentThread.getName(), currentThread.getPriority());
        final MethodEnterEvent methodEnterEvent = new MethodEnterEvent(className, accessFlag, methodName, desc, System.nanoTime());
        sTaskRunningCount.incrementAndGet();
        if (sStopped) {//double check for thread safe
            return;
        }
        sWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>();
                    sMethodEventMap.put(threadInfo, methodEvents);
                }
                MethodCanaryLogger.log("方法进入:" + methodEnterEvent.methodName);
                methodEvents.add(methodEnterEvent);
                sMethodEventOfMapCount = sMethodEventOfMapCount + 1;
                checkShouldWriteMethodEventsToFile(false);
                sTaskRunningCount.decrementAndGet();
            }
        });
    }

    @Keep
    public static void onMethodExit(int accessFlag, String className, final String methodName, String desc) {
        if (sStopped) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        final ThreadInfo threadInfo = new ThreadInfo(currentThread.getId(), currentThread.getName(), currentThread.getPriority());
        final MethodExitEvent methodExitEvent = new MethodExitEvent(className, accessFlag, methodName, desc, System.nanoTime());
        sTaskRunningCount.incrementAndGet();
        if (sStopped) {//double check for thread safe
            return;
        }
        sWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>();
                    sMethodEventMap.put(threadInfo, methodEvents);
                }
                MethodCanaryLogger.log("方法退出:" + methodExitEvent.methodName);
                methodEvents.add(methodExitEvent);
                sMethodEventOfMapCount = sMethodEventOfMapCount + 1;
                checkShouldWriteMethodEventsToFile(false);
                sTaskRunningCount.decrementAndGet();
            }
        });
    }

    private static void checkShouldWriteMethodEventsToFile(boolean isForce) {
        if (isForce || sMethodEventOfMapCount >= sMethodCanaryConfig.methodEventThreshold) {
            try {
                File record = Util.ensureRecordFile(sMethodCanaryConfig.app);
                byte[] content = Util.serializeMethodEvent(sMethodEventMap).getBytes(Charset.forName("utf-8"));
                boolean result = Util.writeFileFromBytesByChannel(record, content, true, false);
                if (!result) {
                    throw new Exception("write method event to file fail.");
                }
                sMethodEventMap.clear();
                sMethodEventOfMapCount = 0;
            } catch (Exception e) {
                MethodCanaryLogger.log("Method event count is over threshold " + sMethodCanaryConfig.methodEventThreshold + ",but write to file fail.");
            }
        }
    }

    public static synchronized void startMonitor() throws Exception {
        if (!sStopped || sTaskRunningCount.get() > 0) {//正在运行中
            MethodCanaryLogger.log("开启监控失败");
            throw new Exception("method canary is monitoring, please wait for monitor's stopping.");
        }
        if (sWorkHandler == null) {
            throw new Exception("please init method canary first.");
        }
        sStopped = false;
        sStartTimeNanos = System.nanoTime();
        MethodCanaryLogger.log("开启监控成功");
    }

    /**
     * record during last start monitor success
     */
    public static synchronized void stopMonitor() {
        sStopped = true;
        sStopTimeNanos = System.nanoTime();
        MethodCanaryLogger.log("结束监控中...");
        if (sWorkHandler != null) {
            sTaskRunningCount.incrementAndGet();
            sWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (sMethodCanaryConfig != null && sMethodCanaryConfig.methodCanaryOutputCallback != null && sMethodCanaryConfig.app != null) {
                        checkShouldWriteMethodEventsToFile(true);
                        sMethodCanaryConfig.methodCanaryOutputCallback.output(sStartTimeNanos, sStopTimeNanos, Util.getRecordFile(sMethodCanaryConfig.app));
                    }
                    clearRuntime();
                    sTaskRunningCount.decrementAndGet();
                    MethodCanaryLogger.log("监控结束.");
                }
            });
        }
    }

    private static void clearRuntime() {
        sStopped = true;
        sStartTimeNanos = 0;
        sStopTimeNanos = 0;
        sMethodEventMap.clear();
        sMethodEventOfMapCount = 0;
        if (sMethodCanaryConfig != null && sMethodCanaryConfig.app != null) {
            Util.deleteRecordFile(sMethodCanaryConfig.app);
        }
        sTaskRunningCount.set(0);
    }
}
