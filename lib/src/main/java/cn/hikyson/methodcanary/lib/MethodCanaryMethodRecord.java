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
     * @param startNanoTime
     * @param stopNanoTime
     * @param methodCanaryConfig
     * @param onGetRecordsCallback
     */
    void getRecords(final long startNanoTime, final long stopNanoTime, final MethodCanaryConfig methodCanaryConfig, final OnGetRecordsCallback onGetRecordsCallback) {
        mTaskQueue.queueTask(new Runnable() {
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
        if (methodEvent.isEnter) {
            if (methodEvent.pairMethodEvent != null) {
                return methodEvent.pairMethodEvent.eventNanoTime - methodEvent.eventNanoTime;
            }
        } else {
            if (methodEvent.pairMethodEvent != null) {
                return methodEvent.eventNanoTime - methodEvent.pairMethodEvent.eventNanoTime;
            }
        }
        return -1;
    }

    void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (mIsRecording) {
            onMethodEventPostProcess(new MethodEvent(className, accessFlag, methodName, desc, true, System.nanoTime(), type));
        }
    }

    void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (mIsRecording) {
            onMethodEventPostProcess(new MethodEvent(className, accessFlag, methodName, desc, false, System.nanoTime(), type));
        }
    }

    private void onMethodEventPostProcess(final MethodEvent methodEvent) {
        Thread currentThread = Thread.currentThread();
        final long id = currentThread.getId();
        final String name = currentThread.getName();
        final int priority = currentThread.getPriority();
        mTaskQueue.queueTask(new Runnable() {
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
                if (methodEvent.isEnter) {
                    methodEventStack.push(methodEvent);
                } else {
                    MethodEvent lastMethodEnterEvent = null;
                    if (!methodEventStack.empty()) {
                        lastMethodEnterEvent = methodEventStack.pop();
                    }
                    if (lastMethodEnterEvent != null) {
                        lastMethodEnterEvent.pairMethodEvent = methodEvent;
                        methodEvent.pairMethodEvent = lastMethodEnterEvent;
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
