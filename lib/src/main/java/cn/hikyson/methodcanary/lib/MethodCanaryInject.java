package cn.hikyson.methodcanary.lib;

import android.app.Application;
import android.support.annotation.Keep;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Keep
public class MethodCanaryInject {
    private static volatile boolean sStopped = true;
    private static AtomicInteger sTaskRunningCount = new AtomicInteger(0);

    private static Map<ThreadInfo, List<MethodEvent>> sMethodEventMap = new HashMap<>();
    private static ThreadPoolExecutor sSingle = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    private static MethodCanaryConfig sMethodCanaryConfig;

    /**
     * init sdk
     *
     * @param methodCanaryConfig
     */
    public static void init(MethodCanaryConfig methodCanaryConfig) {
        sMethodCanaryConfig = methodCanaryConfig;
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
        sSingle.execute(new Runnable() {
            @Override
            public void run() {
                List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>();
                    sMethodEventMap.put(threadInfo, methodEvents);
                }
                MethodCanaryLogger.log("方法进入:" + methodEnterEvent.methodName);
                methodEvents.add(methodEnterEvent);
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
        sSingle.execute(new Runnable() {
            @Override
            public void run() {
                List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>();
                    sMethodEventMap.put(threadInfo, methodEvents);
                }
                MethodCanaryLogger.log("方法退出:" + methodExitEvent.methodName);
                methodEvents.add(methodExitEvent);
                checkShouldWriteMethodEventsToFile(false);
                sTaskRunningCount.decrementAndGet();
            }
        });
    }

    private static void checkShouldWriteMethodEventsToFile(boolean isForce) {
        if (isForce || Util.computeMethodEventCount(sMethodEventMap) > sMethodCanaryConfig.getMethodEventThreshold()) {
            // TODO KYSON DEL log
            MethodCanaryLogger.log("到了！！！" + sMethodCanaryConfig.getMethodEventThreshold());
            try {
                File record = Util.ensureRecordFile(sMethodCanaryConfig.getApp());
                byte[] content = Util.serializeMethodEvent(sMethodEventMap).getBytes(Charset.forName("utf-8"));
                boolean result = Util.writeFileFromBytesByChannel(record, content, true, true);
                if (!result) {
                    throw new Exception("write method event to file fail.");
                }
                // TODO KYSON 为什么没清空？
                sMethodEventMap.clear();
            } catch (Exception ignore) {
            }
        } else {// TODO KYSON DEL log
            MethodCanaryLogger.log("没到" + sMethodCanaryConfig.getMethodEventThreshold());
        }
    }

    public static void startMonitor() throws Exception {
        if (!sStopped || sTaskRunningCount.get() > 0) {//正在运行中
            MethodCanaryLogger.log("开启监控失败");
            throw new Exception("method canary is monitoring, please wait for monitor's stopping.");
        }
        sStopped = false;
        MethodCanaryLogger.log("开启监控成功");
    }

    /**
     * record during last start monitor success
     */
    public static void stopMonitorAndOutput() {
        sStopped = true;
        MethodCanaryLogger.log("结束监控中...");
        sSingle.execute(new Runnable() {
            @Override
            public void run() {
                sMethodCanaryConfig.getMethodCanaryOutputCallback().output(new HashMap<>(sMethodEventMap), Util.getRecordFile(sMethodCanaryConfig.getApp()));
                sMethodEventMap.clear();
                Util.deleteRecordFile(sMethodCanaryConfig.getApp());
                sTaskRunningCount.set(0);
                MethodCanaryLogger.log("监控结束.");
            }
        });
    }
}
