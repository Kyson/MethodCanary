package cn.hikyson.methodcanary.lib;

public class MethodCanary {

    public static MethodCanary get() {
        return new MethodCanary();
    }

    private MethodCanary() {
    }

    public synchronized void start(String sessionTag) {
    }

    public synchronized void stop(final String sessionTag, MethodCanaryConfig methodCanaryConfig, final MethodCanaryOnGetRecordsCallback methodCanaryOnGetRecordsCallback) {
    }

    public synchronized boolean isRunning(final String sessionTag) {
        return false;
    }
}
