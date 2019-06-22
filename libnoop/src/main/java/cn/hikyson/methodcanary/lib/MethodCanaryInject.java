package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

@Keep
public class MethodCanaryInject {

    public static synchronized void init(MethodCanaryConfig methodCanaryConfig) {
    }

    @Keep
    public static void onMethodEnter(int accessFlag, String className, String methodName, String desc) {
    }

    @Keep
    public static void onMethodExit(int accessFlag, String className, final String methodName, String desc) {
    }

    public static synchronized void startMonitor() throws Exception {
    }

    public static synchronized void stopMonitor() {
    }
}
