package cn.hikyson.methodcanary.plugin


public class ClassInfo {
    public int access
    public String name
    public String superName
    public String[] interfaces

    @Override
    public String toString() {
        return "ClassInfo{" +
                "access=" + access +
                ", name='" + name + '\'' +
                ", superName='" + superName + '\'' +
                ", interfaces=" + Arrays.toString(interfaces) +
                '}';
    }
}