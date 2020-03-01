package cn.hikyson.methodcanary.lib;


public class MethodCanary {
    private static class InstanceHolder {
        private static MethodCanary sMethodCanary = new MethodCanary();
    }

    public static MethodCanary get() {
        return InstanceHolder.sMethodCanary;
    }

    private MethodCanary() {
    }

    public void startCommonRecording(String sessionTag) {
    }

    public void stopCommonRecording(final String sessionTag, MethodCanaryConfig methodCanaryConfig, final MethodCanaryOnGetRecordsCallback methodCanaryOnGetRecordsCallback) {
    }

    public boolean isCommonRecorderRunning(final String sessionTag) {
        return false;
    }

    public void setOnPageLifecycleEventCallback(MethodCanaryForSpecial.OnPageLifecycleEventCallback onPageLifecycleEventCallback) {
    }

}
