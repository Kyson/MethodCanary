package cn.hikyson.methodcanary.lib;

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
    private static MethodCanaryTaskQueue sTaskQueue;
    private static MethodCanaryConfig sMethodCanaryConfig;

    public static synchronized boolean isMonitoring() {
        return isMonitoringInternal();
    }

    public static synchronized void startMonitor(MethodCanaryConfig methodCanaryConfig) throws Exception {
        if (isMonitoringInternal()) {//正在运行中
            MethodCanaryLogger.log("开启监控失败");
            throw new Exception("method canary is monitoring, please wait for monitor's stopping.");
        }
        clearRuntime();
        sMethodCanaryConfig = methodCanaryConfig;
        sTaskQueue = new MethodCanaryTaskQueue();
        sTaskQueue.start();
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
        if (sTaskQueue != null) {
            sTaskRunningCount.incrementAndGet();
            if (sMethodCanaryConfig != null && sMethodCanaryConfig.methodCanaryCallback != null) {
                sMethodCanaryConfig.methodCanaryCallback.onStopped(sStartTimeNanos, sStopTimeNanos);
            }
            sTaskQueue.addTask(new Runnable() {
                @Override
                public void run() {
                    long start = sStartTimeNanos;
                    long stop = sStopTimeNanos;
                    Map<ThreadInfo, List<MethodEvent>> copy = new HashMap<>(sMethodEventMap);
                    if (sMethodCanaryConfig != null && sMethodCanaryConfig.methodCanaryCallback != null) {
                        sMethodCanaryConfig.methodCanaryCallback.outputToMemory(start, stop, copy);
                    }
                    sMethodCanaryConfig = null;
                    clearRuntime();
                    MethodCanaryLogger.log("监控结束.");
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

    private static Object[] onMethodEventPrepare() {
        final long eventTimeNanos = System.nanoTime();
        Thread currentThread = Thread.currentThread();
        sTaskRunningCount.incrementAndGet();
        return new Object[]{currentThread.getId(), currentThread.getName(), currentThread.getPriority(), eventTimeNanos};
    }

    private static void onMethodEventPostProcess(final long id, final String name, final int priority, final MethodEvent methodEvent) {
        if (sTaskQueue != null) {
            sTaskQueue.addTask(new Runnable() {
                @Override
                public void run() {
                    final ThreadInfo threadInfo = obtainThreadInfo(id, name, priority);
                    List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                    ThreadInfo copy = null;
                    if (methodEvents == null) {
                        methodEvents = new LinkedList<>();
                        copy = threadInfo.copy();
                        sMethodEventMap.put(copy, methodEvents);
                    }
                    Stack<MethodEvent> methodEventStack = sMethodEventStackMap.get(threadInfo);
                    if (methodEventStack == null) {
                        methodEventStack = new Stack<>();
                        sMethodEventStackMap.put(copy == null ? threadInfo.copy() : copy, methodEventStack);
                    }
                    if (methodEvent instanceof MethodEnterEvent) {
                        methodEventStack.push(methodEvent);
                        addMethodEvent(methodEvents, methodEvent);
                    } else if (methodEvent instanceof MethodExitEvent) {
                        MethodEvent lastMethodEnterEvent = null;
                        try {
                            lastMethodEnterEvent = methodEventStack.pop();
                        } catch (EmptyStackException ignore) {
                        }
                        if (lastMethodEnterEvent != null && (methodEvent.eventNanoTime - lastMethodEnterEvent.eventNanoTime) <= sMethodCanaryConfig.lowCostThreshold) {
                            removeMethodEvent(methodEvents, lastMethodEnterEvent);
                        } else {
                            addMethodEvent(methodEvents, methodEvent);
                        }
                    }
                    sTaskRunningCount.decrementAndGet();
                }
            });
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

    private static void clearRuntime() {
        sStopped = true;
        sStartTimeNanos = 0;
        sStopTimeNanos = 0;
        sMethodEventMap.clear();
        sMethodEventStackMap.clear();
        sMethodEventOfMapCount = 0;
        sTaskRunningCount.set(0);
        if (sTaskQueue != null) {
            sTaskQueue.stop();
            sTaskQueue = null;
        }
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
