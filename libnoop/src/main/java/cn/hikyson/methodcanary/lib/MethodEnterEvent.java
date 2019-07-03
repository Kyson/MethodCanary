package cn.hikyson.methodcanary.lib;

public class MethodEnterEvent extends MethodEvent {

    public MethodEnterEvent(String className, int methodAccessFlag, String methodName, String methodDesc, long eventNanoTime) {
        super(className, methodAccessFlag, methodName, methodDesc, eventNanoTime);
    }

    @Override
    public String toString() {
        return "PUSH:et=" + eventNanoTime + ";cn=" + className + ";ma=" + methodAccessFlag + ";mn=" + methodName + ";md=" + methodDesc;
    }
}
