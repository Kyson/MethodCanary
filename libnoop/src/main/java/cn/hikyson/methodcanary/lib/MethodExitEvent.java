package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

@Keep
public class MethodExitEvent extends MethodEvent {
    public MethodEnterEvent methodEnterEvent;

    public MethodExitEvent(String className, int methodAccessFlag, String methodName, String methodDesc, long eventNanoTime) {
        super(className, methodAccessFlag, methodName, methodDesc, eventNanoTime);
    }

    @Override
    public String toString() {
        return "POP:et=" + eventNanoTime + ";cn=" + className + ";ma=" + methodAccessFlag + ";mn=" + methodName + ";md=" + methodDesc;
    }

    public String pairMethodToString() {
        return String.valueOf(methodEnterEvent);
    }
}
