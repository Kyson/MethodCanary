package cn.hikyson.methodcanary.lib;

public class MethodInfo {
    public static MethodInfo VIRTUAL_METHOD = new MethodInfo(0, "virtual.class", "virtual.method", null);
    public int accessFlag;
    public String className;
    public String methodName;
    public String desc;

    public MethodInfo(int accessFlag, String className, String methodName, String desc) {
        this.accessFlag = accessFlag;
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
    }
}
