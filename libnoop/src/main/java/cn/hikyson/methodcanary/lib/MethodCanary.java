package cn.hikyson.methodcanary.lib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodCanary {

    private static class InstanceHolder {
        private static MethodCanary sMethodCanary = new MethodCanary();
    }

    public static MethodCanary get() {
        return InstanceHolder.sMethodCanary;
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
