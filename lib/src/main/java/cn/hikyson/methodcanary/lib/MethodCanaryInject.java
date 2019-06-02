package cn.hikyson.methodcanary.lib;

public class MethodCanaryInject {
    private static boolean isInjecting;

    public static void onMethodEnter(String clazz, String methodAccess, String methodDesc) {
        if (!isInjecting) {
            return;
        }
    }

    public static void onMethodExit() {
        if (!isInjecting) {
            return;
        }
    }

}
