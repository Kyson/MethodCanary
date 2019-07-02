package cn.hikyson.methodcanary.lib;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface MethodCanaryCallback {
    void onStopped();

    void outputToMemory(long startTimeNanos, long stopTimeNanos, Map<ThreadInfo, List<MethodEvent>> methodEventMap);

    void outputToFile(long startTimeNanos, long stopTimeNanos, File methodEventsFile);
}
