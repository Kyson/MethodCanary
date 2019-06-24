package cn.hikyson.methodcanary.lib;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface MethodCanaryOutputCallback {
    void output(File methodEventsFile);
}
