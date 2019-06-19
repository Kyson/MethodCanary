package cn.hikyson.methodcanary.plugin

public class MethodInfo {
    int access
    String name
    String desc

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