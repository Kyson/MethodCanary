package cn.hikyson.methodcanary.lib;


public class MethodCanary {
    private static class InstanceHolder {
        private static MethodCanary sMethodCanary = new MethodCanary();
    }

    public static MethodCanary get() {
        return InstanceHolder.sMethodCanary;
    }

    MethodCanaryForCommon methodCanaryForCommon;
    MethodCanaryForSpecial methodCanaryForSpecial;

    private MethodCanary() {
        methodCanaryForCommon = new MethodCanaryForCommon();
        methodCanaryForSpecial = new MethodCanaryForSpecial();
    }


    public void startCommonRecording(String sessionTag) {
        methodCanaryForCommon.start(sessionTag);
    }

    public void stopCommonRecording(final String sessionTag, MethodCanaryConfig methodCanaryConfig, final MethodCanaryOnGetRecordsCallback methodCanaryOnGetRecordsCallback) {
        methodCanaryForCommon.stop(sessionTag, methodCanaryConfig, methodCanaryOnGetRecordsCallback);
    }

    public boolean isCommonRecorderRunning(final String sessionTag) {
        return methodCanaryForCommon.isRunning(sessionTag);
    }

    public void setOnPageLifecycleEventCallback(MethodCanaryForSpecial.OnPageLifecycleEventCallback onPageLifecycleEventCallback) {
        methodCanaryForSpecial.setOnPageLifecycleEventCallback(onPageLifecycleEventCallback);
    }

}
