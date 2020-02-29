package cn.hikyson.methodcanary.plugin

class InternalExcludes {

    boolean isMethodExclude(ClassInfo classInfo, MethodInfo methodInfo) {
        return classInfo.name.startsWith('cn/hikyson/methodcanary/lib/') || classInfo.name.startsWith('cn/hikyson/godeye/core/') || classInfo.name.startsWith('cn/hikyson/godeye/monitor/') || classInfo.name.startsWith('cn/hikyson/android/godeye/')
    }
}