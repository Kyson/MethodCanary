package cn.hikyson.methodcanary.plugin

class InternalExcludes {

    boolean isMethodExclude(ClassInfo classInfo, MethodInfo methodInfo) {
        return classInfo.name.startsWith('cn/hikyson/methodcanary/lib/')
    }
}