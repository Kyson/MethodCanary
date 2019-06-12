package cn.hikyson.methodcanary.lib;

public class MethodCanaryInject {
    private static volatile boolean sIsMonitoring;
    private static ThreadLocal<MethodTree> sMethodTrees = new ThreadLocal<>();

    public static void onMethodEnter(int accessFlag, String className, String methodName, String desc) {
        if (!sIsMonitoring) {
            return;
        }
        MethodTree methodTree = sMethodTrees.get();
        if (methodTree == null) {
            methodTree = new MethodTree(Thread.currentThread());
            sMethodTrees.set(methodTree);
        }
        methodTree.pushMethod(new MethodInfo(accessFlag, className, methodName, desc));
    }

    public static void onMethodExit() {
        if (!sIsMonitoring) {
            return;
        }
        MethodTree methodTree = sMethodTrees.get();
        if (methodTree != null) {
            methodTree.popMethod();
        }
    }

    public static void startMonitor() {
        sIsMonitoring = true;
    }

    public static void stopMonitor() {
        sIsMonitoring = false;
    }
}
