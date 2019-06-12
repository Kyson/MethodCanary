package cn.hikyson.methodcanary.lib;

import static cn.hikyson.methodcanary.lib.MethodInfo.VIRTUAL_METHOD;

public class MethodTraceInfo {
    public static MethodTraceInfo VIRTUAL_TOP_NODE = new MethodTraceInfo(0, VIRTUAL_METHOD);
    public long startTimeNano;
    public long endTimeNano;
    public MethodInfo methodInfo;

    public MethodTraceInfo(long startTimeNano, MethodInfo methodInfo) {
        this.startTimeNano = startTimeNano;
        this.methodInfo = methodInfo;
    }
}
