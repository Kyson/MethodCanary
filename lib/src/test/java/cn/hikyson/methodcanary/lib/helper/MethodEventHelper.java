package cn.hikyson.methodcanary.lib.helper;

import cn.hikyson.methodcanary.lib.MethodEvent;

public class MethodEventHelper {

    public static MethodEvent createRandomMethodEvent() {
        String className = "cn/hikyson/classA";
        int methodAccessFlag = 0;
        String methodName = "methodNameA";
        String methodDesc = "I[]";
        boolean isEnter = true;
        long eventNanoTime = System.nanoTime();
        MethodEvent methodEvent = new MethodEvent(className, methodAccessFlag, methodName, methodDesc, isEnter, eventNanoTime, 0);
        return methodEvent;
    }

}
