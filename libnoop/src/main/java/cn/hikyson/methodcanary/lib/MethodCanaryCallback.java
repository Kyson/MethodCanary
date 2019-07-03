package cn.hikyson.methodcanary.lib;

import java.util.List;
import java.util.Map;

public interface MethodCanaryCallback {
    void onStopped(long startTimeNanos, long stopTimeNanos);

    void outputToMemory(long startTimeNanos, long stopTimeNanos, Map<ThreadInfo, List<MethodEvent>> methodEventMap);
}
