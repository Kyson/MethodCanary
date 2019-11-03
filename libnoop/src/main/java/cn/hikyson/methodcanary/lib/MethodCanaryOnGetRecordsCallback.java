package cn.hikyson.methodcanary.lib;

import java.util.List;
import java.util.Map;

public interface MethodCanaryOnGetRecordsCallback {
    void onGetRecords(String sessionTag, long startNanoTime, long stopNanoTime, Map<ThreadInfo, List<MethodEvent>> methodEventMap);
}
