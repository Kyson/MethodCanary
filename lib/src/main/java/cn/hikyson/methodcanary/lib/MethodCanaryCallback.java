package cn.hikyson.methodcanary.lib;

import java.util.List;
import java.util.Map;

public interface MethodCanaryCallback {
    void onStopped(long startTimeMillis, long stopTimeMillis);

    void outputToMemory(long startTimeMillis, long stopTimeMillis, Map<ThreadInfo, List<MethodEvent>> methodEventMap);
}
