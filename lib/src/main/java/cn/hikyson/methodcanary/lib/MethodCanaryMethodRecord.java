package cn.hikyson.methodcanary.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class MethodCanaryMethodRecord {
    private volatile boolean mIsRecording;
    private Map<ThreadInfo, List<MethodEvent>> mMethodEventMap = new HashMap<>();
    private Map<ThreadInfo, Stack<MethodEvent>> mMethodEventStackMap = new HashMap<>();
    private MethodCanaryTaskQueue mTaskQueue;

    MethodCanaryMethodRecord(MethodCanaryTaskQueue taskQueue) {
        this.mIsRecording = false;
        this.mTaskQueue = taskQueue;
    }

    void startRecordRightNow() {
        mIsRecording = true;
    }

    void stopRecordRightNow() {
        mIsRecording = false;
    }

    interface OnGetRecordsCallback {
        void onGetRecords(Map<ThreadInfo, List<MethodEvent>> methodEventMap);
    }

    /**
     * get method recordings in time range
     *
     * @param startNanoTime
     * @param stopNanoTime
     * @param methodCanaryConfig
     * @return
     */
    void getRecords(final long startNanoTime, final long stopNanoTime, final MethodCanaryConfig methodCanaryConfig, final OnGetRecordsCallback onGetRecordsCallback) {
        mTaskQueue.addTask(new Runnable() {
            @Override
            public void run() {
                Map<ThreadInfo, List<MethodEvent>> filtered = new HashMap<>();
                for (Map.Entry<ThreadInfo, List<MethodEvent>> entry : mMethodEventMap.entrySet()) {
                    List<MethodEvent> filteredMethodEventsThread = new ArrayList<>();
                    for (MethodEvent methodEvent : entry.getValue()) {
                        if (methodEvent.eventNanoTime >= startNanoTime
                                && methodEvent.eventNanoTime <= stopNanoTime) {
                            long methodCost = getMethodEventCost(methodEvent);
                            if (methodCost < 0 || methodCost > methodCanaryConfig.lowCostThresholdNanoTime) {
                                filteredMethodEventsThread.add(methodEvent);
                            }
                        }
                    }
                    if (!filteredMethodEventsThread.isEmpty()) {
                        filtered.put(entry.getKey(), filteredMethodEventsThread);
                    }
                }
                if (onGetRecordsCallback != null) {
                    onGetRecordsCallback.onGetRecords(filtered);
                }
            }
        });
    }

    private long getMethodEventCost(MethodEvent methodEvent) {
        if (methodEvent instanceof MethodEnterEvent) {
            MethodExitEvent pair = ((MethodEnterEvent) methodEvent).methodExitEvent;
            if (pair != null) {
                return pair.eventNanoTime - methodEvent.eventNanoTime;
            }
        } else if (methodEvent instanceof MethodExitEvent) {
            MethodEnterEvent pair = ((MethodExitEvent) methodEvent).methodEnterEvent;
            if (pair != null) {
                return methodEvent.eventNanoTime - pair.eventNanoTime;
            }
        }
        return -1;
    }

    void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc) {
        if (mIsRecording) {
            final long eventTimeNanos = System.nanoTime();
            onMethodEventPostProcess(new MethodEnterEvent(className, accessFlag, methodName, desc, eventTimeNanos));
        }
    }

    void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc) {
        if (mIsRecording) {
            final long eventTimeNanos = System.nanoTime();
            onMethodEventPostProcess(new MethodExitEvent(className, accessFlag, methodName, desc, eventTimeNanos));
        }
    }

    private void onMethodEventPostProcess(final MethodEvent methodEvent) {
        Thread currentThread = Thread.currentThread();
        final long id = currentThread.getId();
        final String name = currentThread.getName();
        final int priority = currentThread.getPriority();
        mTaskQueue.addTask(new Runnable() {
            @Override
            public void run() {
                final ThreadInfo threadInfo = obtainThreadInfo(id, name, priority);
                List<MethodEvent> methodEvents = mMethodEventMap.get(threadInfo);
                ThreadInfo copy = null;
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>(128);
                    copy = threadInfo.copy();
                    mMethodEventMap.put(copy, methodEvents);
                }
                Stack<MethodEvent> methodEventStack = mMethodEventStackMap.get(threadInfo);
                if (methodEventStack == null) {
                    methodEventStack = new Stack<>();
                    mMethodEventStackMap.put(copy == null ? threadInfo.copy() : copy, methodEventStack);
                }
                methodEvents.add(methodEvent);
                if (methodEvent instanceof MethodEnterEvent) {
                    methodEventStack.push(methodEvent);
                } else if (methodEvent instanceof MethodExitEvent) {
                    MethodEvent lastMethodEnterEvent = null;
                    if (!methodEventStack.empty()) {
                        lastMethodEnterEvent = methodEventStack.pop();
                    }
                    if (lastMethodEnterEvent != null) {
                        ((MethodEnterEvent) lastMethodEnterEvent).methodExitEvent = (MethodExitEvent) methodEvent;
                        ((MethodExitEvent) methodEvent).methodEnterEvent = (MethodEnterEvent) lastMethodEnterEvent;
                    }
                }
            }
        });
    }

    private ThreadInfo mThreadInfoInstance = new ThreadInfo();

    /**
     * only for single thread
     *
     * @param id
     * @param name
     * @param priority
     * @return
     */
    private ThreadInfo obtainThreadInfo(long id, String name, int priority) {
        mThreadInfoInstance.id = id;
        mThreadInfoInstance.name = name;
        mThreadInfoInstance.priority = priority;
        return mThreadInfoInstance;
    }
}
