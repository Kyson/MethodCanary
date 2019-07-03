package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

@Keep
public class MethodCanaryInject {
    /**
     * install sdk
     *
     * @param methodCanaryConfig
     */
    public static synchronized void install(MethodCanaryConfig methodCanaryConfig) {
    }

    /**
     * uninstall
     */
    public static synchronized void uninstall() {
    }

    public static synchronized void startMonitor() throws Exception {
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
