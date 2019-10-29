package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public class MethodEvent implements Serializable {
    public String className;
    public int methodAccessFlag;
    public String methodName;
    public String methodDesc;
    public long eventNanoTime;

    public MethodEvent(String className, int methodAccessFlag, String methodName, String methodDesc, long eventNanoTime) {
        this.className = className;
        this.methodAccessFlag = methodAccessFlag;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.eventNanoTime = eventNanoTime;
    }
}
