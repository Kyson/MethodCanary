package cn.hikyson.methodcanary.plugin

public class MethodInfo {
    public int access
    public String name
    public String desc

    MethodInfo(int access, String name, String desc) {
        this.access = access
        this.name = name
        this.desc = desc
    }


    @Override
    public String toString() {
        return "MethodInfo{" +
                "access=" + access +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}