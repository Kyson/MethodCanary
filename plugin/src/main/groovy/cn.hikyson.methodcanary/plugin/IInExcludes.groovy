package cn.hikyson.methodcanary.plugin

public interface IInExcludes {
    public boolean isMethodInclude(ClassInfo classInfo, MethodInfo methodInfo);
    public boolean isMethodExclude(ClassInfo classInfo, MethodInfo methodInfo);
}