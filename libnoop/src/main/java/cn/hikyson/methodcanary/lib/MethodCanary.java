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

    public void startMethodTracing(String sessionTag) {
    }

    public void stopMethodTracing(final String sessionTag, MethodCanaryConfig methodCanaryConfig, final MethodCanaryOnGetRecordsCallback methodCanaryOnGetRecordsCallback) {
    }

    public boolean isMethodTraceRunning(final String sessionTag) {
        return false;
    }

    public void setOnPageLifecycleEventCallback(OnPageLifecycleEventCallback onPageLifecycleEventCallback) {
    }

}
