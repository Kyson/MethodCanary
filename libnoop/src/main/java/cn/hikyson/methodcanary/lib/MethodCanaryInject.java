package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

@Keep
public class MethodCanaryInject {

    public static synchronized boolean isMonitoring() {
        return false;
    }

    public static synchronized void startMonitor(MethodCanaryConfig methodCanaryConfig) throws Exception {
    }

    /**
     * record during last start monitor success
     */
    public static synchronized void stopMonitor() {
    }

    @Keep
    public static void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc) {
    }

    @Keep
    public static void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc) {
    }
}
