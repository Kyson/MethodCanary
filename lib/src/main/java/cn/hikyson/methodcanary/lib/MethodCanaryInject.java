package cn.hikyson.methodcanary.lib;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Keep;

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
    private static Map<ThreadInfo, Stack<MethodEvent>> sMethodEventStackMap = new HashMap<>();
    private static Handler sWorkHandler;
    private static MethodCanaryConfig sMethodCanaryConfig;
    private static final int METHOD_COUNT_INIT_SIZE = 32;

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

    public static synchronized boolean isMonitoring() {
        return isMonitoringInternal();
    }

    public static synchronized boolean isInstalled() {
        return isInstalledInternal();
    }

    public static synchronized void startMonitor() throws Exception {
        if (isMonitoringInternal()) {//正在运行中
//            MethodCanaryLogger.log("开启监控失败");
            throw new Exception("method canary is monitoring, please wait for monitor's stopping.");
        }
        if (!isInstalledInternal()) {
            throw new Exception("please install method canary first.");
        }
        sStartTimeNanos = System.nanoTime();
        sStopped = false;
//        MethodCanaryLogger.log("开启监控成功");
    }


    /**
     * record during last start monitor success
     */
    public static synchronized void stopMonitor() {
        sStopTimeNanos = System.nanoTime();
        sStopped = true;
//        MethodCanaryLogger.log("结束监控中...");
        if (sWorkHandler != null) {
            sTaskRunningCount.incrementAndGet();
            if (sMethodCanaryConfig != null && sMethodCanaryConfig.methodCanaryCallback != null) {
                sMethodCanaryConfig.methodCanaryCallback.onStopped(sStartTimeNanos, sStopTimeNanos);
            }
            sWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (sMethodCanaryConfig != null && sMethodCanaryConfig.methodCanaryCallback != null) {
                        sMethodCanaryConfig.methodCanaryCallback.outputToMemory(sStartTimeNanos, sStopTimeNanos, sMethodEventMap);
                    }
                    clearRuntime();
//                    MethodCanaryLogger.log("监控结束.");
                }
            });
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
        onMethodEventPostProcess((long) params[0], (String) params[1], (int) params[2], new MethodEnterEvent(className, accessFlag, methodName, desc, (long) params[3]));
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
        onMethodEventPostProcess((long) params[0], (String) params[1], (int) params[2], new MethodExitEvent(className, accessFlag, methodName, desc, (long) params[3]));
    }

    private static boolean isMonitoringInternal() {
        return (!sStopped || sTaskRunningCount.get() > 0);
    }

    private static boolean isInstalledInternal() {
        return sWorkHandler != null;
    }

    private static Object[] onMethodEventPrepare() {
        final long eventTimeNanos = System.nanoTime();
        Thread currentThread = Thread.currentThread();
        sTaskRunningCount.incrementAndGet();
        return new Object[]{currentThread.getId(), currentThread.getName(), currentThread.getPriority(), eventTimeNanos};
    }

    private static void onMethodEventPostProcess(final long id, final String name, final int priority, final MethodEvent methodEvent) {
        if (sWorkHandler != null) {
            sWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    final ThreadInfo threadInfo = obtainThreadInfo(id, name, priority);
                    List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                    ThreadInfo copy = null;
                    if (methodEvents == null) {
                        methodEvents = new ArrayList<>(METHOD_COUNT_INIT_SIZE);
                        copy = threadInfo.copy();
                        sMethodEventMap.put(copy, methodEvents);
                    }
                    Stack<MethodEvent> methodEventStack = sMethodEventStackMap.get(threadInfo);
                    if (methodEventStack == null) {
                        methodEventStack = new Stack<>();
                        sMethodEventStackMap.put(copy == null ? threadInfo.copy() : copy, methodEventStack);
                    }
//                    MethodCanaryLogger.log(String.format("开始处理方法事件，当前线程:%s", threadInfo));
                    if (methodEvent instanceof MethodEnterEvent) {
                        methodEventStack.push(methodEvent);
                        addMethodEvent(methodEvents, methodEvent);
//                        MethodCanaryLogger.log(String.format("本次PUSH方法[%s]添加进来", methodEvent.methodName));
                    } else if (methodEvent instanceof MethodExitEvent) {
                        MethodEvent lastMethodEnterEvent = null;
                        try {
                            lastMethodEnterEvent = methodEventStack.pop();
                        } catch (EmptyStackException ignore) {
                        }
                        if (lastMethodEnterEvent != null && (methodEvent.eventNanoTime - lastMethodEnterEvent.eventNanoTime) <= sMethodCanaryConfig.lowCostThreshold) {
                            removeMethodEvent(methodEvents, lastMethodEnterEvent);
//                            checkMethodEvent(lastMethodEnterEvent, methodEvent);
//                            MethodCanaryLogger.log(String.format("本次POP方法[%s]和上次PUSH方法[%s]被排除", methodEvent.methodName, lastMethodEnterEvent.methodName));
                        } else {
                            addMethodEvent(methodEvents, methodEvent);
//                            MethodCanaryLogger.log(String.format("本次POP方法[%s]添加进来", methodEvent.methodName));
                        }
                    }
//                    MethodCanaryLogger.log(String.format("结束处理方法事件，当前线程:%s", threadInfo));
                    sTaskRunningCount.decrementAndGet();
                }
            });
        }
    }

    private static void checkMethodEvent(MethodEvent first, MethodEvent second) {
        if (!first.className.equals(second.className)
                || !first.methodName.equals(second.methodName)
                || !first.methodDesc.equals(second.methodDesc)
                || first.methodAccessFlag != second.methodAccessFlag) {
            throw new IllegalStateException("checkMethodEvent");
        }
    }

    private static void addMethodEvent(List<MethodEvent> methodEvents, MethodEvent methodEvent) {
        methodEvents.add(methodEvent);
        sMethodEventOfMapCount = sMethodEventOfMapCount + 1;
    }

    private static void removeMethodEvent(List<MethodEvent> methodEvents, MethodEvent methodEvent) {
        methodEvents.remove(methodEvent);
        sMethodEventOfMapCount = sMethodEventOfMapCount - 1;
    }

//    private static boolean isNeedWriteToFile() {
//        return sMethodCanaryConfig.methodEventThreshold > 0;
//    }
//
//    private static void checkShouldWriteMethodEventsToFile(boolean isForce) {
//        if (isNeedWriteToFile() && (isForce || sMethodEventOfMapCount >= sMethodCanaryConfig.methodEventThreshold)) {
//            try {
//                File record = Util.ensureRecordFile(sMethodCanaryConfig.app);
//                long start = System.currentTimeMillis();
//                boolean result = Util.mergeInToFile(record, sMethodEventMap);
//                MethodCanaryLogger.log(String.format("超出方法数限制[%s]或强制合并进文件,结果[%s],消耗[%s]ms", sMethodCanaryConfig.methodEventThreshold, result, (System.currentTimeMillis() - start)));
//                if (!result) {
//                    throw new Exception("write method event to file fail.");
//                }
//                sMethodEventMap.clear();
//                sMethodEventOfMapCount = 0;
//            } catch (Exception e) {
//                MethodCanaryLogger.log("Method event count is over threshold " + sMethodCanaryConfig.methodEventThreshold + ",but write to file fail.");
//            }
//        }
//    }

    private static void clearRuntime() {
        sStopped = true;
        sStartTimeNanos = 0;
        sStopTimeNanos = 0;
        sMethodEventMap.clear();
        sMethodEventStackMap.clear();
        sMethodEventOfMapCount = 0;
        sTaskRunningCount.set(0);
    }

    private static ThreadInfo sThreadInfoInstance = new ThreadInfo();

    /**
     * only for single thread
     *
     * @param id
     * @param name
     * @param priority
     * @return
     */
    private static ThreadInfo obtainThreadInfo(long id, String name, int priority) {
        sThreadInfoInstance.id = id;
        sThreadInfoInstance.name = name;
        sThreadInfoInstance.priority = priority;
        return sThreadInfoInstance;
    }
}
