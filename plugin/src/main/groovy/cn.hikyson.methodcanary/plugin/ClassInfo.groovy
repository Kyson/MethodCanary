package cn.hikyson.methodcanary.plugin


public class ClassInfo {
    int access
    String name
    String superName
    String[] interfaces


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