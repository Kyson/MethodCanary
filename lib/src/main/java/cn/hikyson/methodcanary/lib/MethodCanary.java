package cn.hikyson.methodcanary.lib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodCanary {
    private MethodCanaryTaskQueue mTaskQueue;
    private HashMap<String, Long> mMethodCanarySessionInfos = new HashMap<>();
    MethodCanaryMethodRecord methodCanaryMethodRecord;

    private static class InstanceHolder {
        private static MethodCanary sMethodCanary = new MethodCanary();
    }

    public static MethodCanary get() {
        return InstanceHolder.sMethodCanary;
    }

    private MethodCanary() {
        mTaskQueue = new MethodCanaryTaskQueue();
        methodCanaryMethodRecord = new MethodCanaryMethodRecord(mTaskQueue);
    }

    public synchronized void start(String sessionTag) {
        long startNanoTime = System.nanoTime();
        MethodCanaryLogger.log("[MethodCanary] start session:" + sessionTag + ", startNanoTime:" + startNanoTime);
        if (mMethodCanarySessionInfos.containsKey(sessionTag)) {
            throw new IllegalStateException("can not start same session.");
        }
        mMethodCanarySessionInfos.put(sessionTag, startNanoTime);
        mTaskQueue.start();
        methodCanaryMethodRecord.startRecordRightNow();
    }

    public synchronized void stop(final String sessionTag, MethodCanaryConfig methodCanaryConfig, final MethodCanaryOnGetRecordsCallback methodCanaryOnGetRecordsCallback) {
        final long stopNanoTime = System.nanoTime();
        MethodCanaryLogger.log("[MethodCanary] stop session:" + sessionTag + ", stopNanoTime:" + stopNanoTime);
        final Long startNanoTime = mMethodCanarySessionInfos.remove(sessionTag);
        if (startNanoTime == null || startNanoTime <= 0) {
            throw new IllegalStateException("can not stop because session not started.");
        }
        methodCanaryMethodRecord.getRecords(startNanoTime, stopNanoTime, methodCanaryConfig, new MethodCanaryMethodRecord.OnGetRecordsCallback() {
            @Override
            public void onGetRecords(Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                if (methodCanaryOnGetRecordsCallback != null) {
                    methodCanaryOnGetRecordsCallback.onGetRecords(sessionTag, startNanoTime, stopNanoTime, methodEventMap);
                }
                MethodCanaryLogger.log("[MethodCanary] on get records for session:" + sessionTag
                        + ", startNanoTime:" + startNanoTime + ", stopNanoTime:" + stopNanoTime + ", thread count:" + methodEventMap.size());
            }
        });
        if (mMethodCanarySessionInfos.isEmpty()) {
            methodCanaryMethodRecord.stopRecordRightNow();
            MethodCanaryLogger.log("[MethodCanary] stopping all.");
        }
    }

    public synchronized boolean isRunning(final String sessionTag) {
        return mMethodCanarySessionInfos.containsKey(sessionTag);
    }
}
