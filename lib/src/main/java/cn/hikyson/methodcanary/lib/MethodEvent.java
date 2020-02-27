package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public class MethodEvent implements Serializable {
    public String className;
    public int methodAccessFlag;
    public String methodName;
    public String methodDesc;
    public boolean isEnter;
    public long eventNanoTime;
    public MethodEvent pairMethodEvent;

    public MethodEvent(String className, int methodAccessFlag, String methodName, String methodDesc, boolean isEnter, long eventNanoTime) {
        this.className = className;
        this.methodAccessFlag = methodAccessFlag;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.isEnter = isEnter;
        this.eventNanoTime = eventNanoTime;
    }

    @Override
    public String toString() {
        return "MethodEvent{" +
                "className='" + className + '\'' +
                ", methodAccessFlag=" + methodAccessFlag +
                ", methodName='" + methodName + '\'' +
                ", methodDesc='" + methodDesc + '\'' +
                ", isEnter=" + isEnter +
                ", eventNanoTime=" + eventNanoTime +
                '}';
    }

    public String toFormatString() {
        return (isEnter ? "PU" : "PO") + ":et=" + eventNanoTime + ";cn=" + className
                + ";ma=" + methodAccessFlag + ";mn="
                + methodName + ";md=" + methodDesc;
    }
}
