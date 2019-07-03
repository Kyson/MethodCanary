package cn.hikyson.methodcanary.lib;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Keep;
import android.support.v4.util.Pools;

import java.io.File;
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
    private static final int METHOD_COUNT_INIT_SIZE = 32;
    private static Pools.SimplePool<ThreadInfo> sThreadInfoSimplePool;

    /**
     * install sdk
     *
     * @param methodCanaryConfig
     */
    public static synchronized void install(MethodCanaryConfig methodCanaryConfig) {
        sMethodCanaryConfig = methodCanaryConfig;
        clearRuntime();
        sThreadInfoSimplePool = new Pools.SimplePool<>(20);
        HandlerThread worker = new HandlerThread("method-canary-record");
        worker.start();
        sWorkHandler = new Handler(worker.getLooper());
    }

    /**
     * uninstall
     */
    public static synchronized void uninstall() {
        clearRuntime();
        sThreadInfoSimplePool = null;
        if (sWorkHandler != null) {
            sWorkHandler.getLooper().quit();
            sWorkHandler = null;
        }
    }

    @Keep
    public static void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc) {
        if (sStopped) {
            return;
        }
        final Object[] params = onMethodEventPrepare();
        if (sStopped) {//double check for thread safe
            return;
        }
        onMethodEventPostProcess((long) params[0], (String) params[1], (int) params[2], new MethodExitEvent(className, accessFlag, methodName, desc, (long) params[3]));
    }

    @Keep
    public static void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc) {
        if (sStopped) {
            return;
        }
        final Object[] params = onMethodEventPrepare();
        if (sStopped) {//double check for thread safe
            return;
        }
        onMethodEventPostProcess((long) params[0], (String) params[1], (int) params[2], new MethodEnterEvent(className, accessFlag, methodName, desc, (long) params[3]));
    }

    private static Object[] onMethodEventPrepare() {
        final long eventTimeNanos = System.nanoTime();
        Thread currentThread = Thread.currentThread();
        sTaskRunningCount.incrementAndGet();
        return new Object[]{currentThread.getId(), currentThread.getName(), currentThread.getPriority(), eventTimeNanos};
    }

    private static void onMethodEventPostProcess(final long id, final String name, final int priority, final MethodEvent methodEvent) {
        sWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                final ThreadInfo threadInfo = obtainThreadInfo(id, name, priority);
                List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>(METHOD_COUNT_INIT_SIZE);
                    sMethodEventMap.put(threadInfo.copy(), methodEvents);
                }
                releaseThreadInfo(threadInfo);
                methodEvents.add(methodEvent);
                sMethodEventOfMapCount = sMethodEventOfMapCount + 1;
                checkShouldWriteMethodEventsToFile(false);
                sTaskRunningCount.decrementAndGet();
            }
        });
    }

    private static void checkShouldWriteMethodEventsToFile(boolean isForce) {
        if (sMethodCanaryConfig.methodEventThreshold > 0 && (isForce || sMethodEventOfMapCount >= sMethodCanaryConfig.methodEventThreshold)) {
            try {
                File record = Util.ensureRecordFile(sMethodCanaryConfig.app);
                long start = System.currentTimeMillis();
                boolean result = Util.mergeInToFile(record, sMethodEventMap);
                MethodCanaryLogger.log(String.format("超出方法数限制[%s]或强制合并进文件,结果[%s],消耗[%s]ms", sMethodCanaryConfig.methodEventThreshold, result, (System.currentTimeMillis() - start)));
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
        sStartTimeNanos = System.nanoTime();
        sStopped = false;
        MethodCanaryLogger.log("开启监控成功");
    }

    /**
     * record during last start monitor success
     */
    public static synchronized void stopMonitor() {
        sStopTimeNanos = System.nanoTime();
        sStopped = true;
        MethodCanaryLogger.log("结束监控中...");
        if (sWorkHandler != null) {
            sTaskRunningCount.incrementAndGet();
            sWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (sMethodCanaryConfig != null && sMethodCanaryConfig.methodCanaryCallback != null && sMethodCanaryConfig.app != null) {
                        sMethodCanaryConfig.methodCanaryCallback.onStopped();
                        if (sMethodCanaryConfig.methodEventThreshold > 0) {//需要写文件
                            checkShouldWriteMethodEventsToFile(true);
                            sMethodCanaryConfig.methodCanaryCallback.outputToFile(sStartTimeNanos, sStopTimeNanos, Util.getRecordFile(sMethodCanaryConfig.app));
                        } else {
                            sMethodCanaryConfig.methodCanaryCallback.outputToMemory(sStartTimeNanos, sStopTimeNanos, sMethodEventMap);
                        }
                    }
                    clearRuntime();
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

    private static ThreadInfo obtainThreadInfo(long id, String name, int priority) {
        ThreadInfo threadInfo = null;
        if (sThreadInfoSimplePool != null) {
            threadInfo = sThreadInfoSimplePool.acquire();
        }
        if (threadInfo == null) {
            threadInfo = new ThreadInfo();
//            MethodCanaryLogger.log("创建ThreadInfo对象");
        } else {
//            MethodCanaryLogger.log("复用ThreadInfo对象");
        }
        threadInfo.id = id;
        threadInfo.name = name;
        threadInfo.priority = priority;
        return threadInfo;
    }

    private static void releaseThreadInfo(ThreadInfo threadInfo) {
        sThreadInfoSimplePool.release(threadInfo);
//        MethodCanaryLogger.log("归还ThreadInfo对象");
    }

}
