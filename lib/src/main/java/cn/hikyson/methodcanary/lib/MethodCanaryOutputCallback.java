package cn.hikyson.methodcanary.lib;

import java.io.File;

public interface MethodCanaryOutputCallback {
    void output(long startTimeNanos, long stopTimeNanos, File methodEventsFile);
}
