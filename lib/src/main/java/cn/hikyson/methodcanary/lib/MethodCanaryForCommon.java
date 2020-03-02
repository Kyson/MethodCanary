package cn.hikyson.methodcanary.lib;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MethodCanaryForCommon {
    private MethodCanaryTaskQueue mTaskQueue;
    private HashMap<String, Long> mMethodCanarySessionInfos = new HashMap<>();
    private MethodCanaryMethodRecord methodCanaryMethodRecord;

    MethodCanaryForCommon() {
        mTaskQueue = new MethodCanaryTaskQueue("AndroidGodEye-MethodCanary-Record");
        methodCanaryMethodRecord = new MethodCanaryMethodRecord(mTaskQueue);
    }

    synchronized void start(String sessionTag) {
        long startNanoTime = System.nanoTime();
        MethodCanaryLogger.log("[MethodCanary] start session:" + sessionTag + ", startNanoTime:" + startNanoTime);
        if (mMethodCanarySessionInfos.containsKey(sessionTag)) {
            throw new IllegalStateException("can not start same session.");
        }
        mMethodCanarySessionInfos.put(sessionTag, startNanoTime);
        mTaskQueue.start();
        methodCanaryMethodRecord.startRecordRightNow();
    }

    synchronized void stop(final String sessionTag, MethodCanaryConfig methodCanaryConfig, final MethodCanaryOnGetRecordsCallback methodCanaryOnGetRecordsCallback) {
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

    synchronized boolean isRunning(final String sessionTag) {
        return mMethodCanarySessionInfos.containsKey(sessionTag);
    }

    void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        methodCanaryMethodRecord.onMethodEnter(accessFlag, className, methodName, desc, type, objs);
    }

    void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        methodCanaryMethodRecord.onMethodExit(accessFlag, className, methodName, desc, type, objs);
    }

    MethodCanaryMethodRecord getMethodCanaryMethodRecord() {
        return methodCanaryMethodRecord;
    }

    void setMethodCanaryMethodRecord(MethodCanaryMethodRecord methodCanaryMethodRecord) {
        this.methodCanaryMethodRecord = methodCanaryMethodRecord;
    }

}
