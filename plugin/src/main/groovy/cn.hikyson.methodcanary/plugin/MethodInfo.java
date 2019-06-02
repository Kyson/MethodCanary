package cn.hikyson.methodcanary.plugin;

import java.util.Arrays;

public class MethodInfo {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public String[] exceptions;

    public MethodInfo(int access, String name, String desc, String signature, String[] exceptions) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "access=" + access +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", signature='" + signature + '\'' +
                ", exceptions=" + Arrays.toString(exceptions) +
                '}';
    }
}
