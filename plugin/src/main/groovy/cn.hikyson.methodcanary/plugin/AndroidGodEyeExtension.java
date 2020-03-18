package cn.hikyson.methodcanary.plugin;


import java.util.List;

public class AndroidGodEyeExtension {
    public boolean enableMethodTracer;
    public boolean enableLifecycleTracer;
    public String instrumentationRuleFilePath;
    public List<String> instrumentationRuleIncludeClassNamePrefix;

    public AndroidGodEyeExtension() {
        this.enableMethodTracer = true;
        this.enableLifecycleTracer = true;
        this.instrumentationRuleFilePath = "AndroidGodEye-MethodCanary.js";
        this.instrumentationRuleIncludeClassNamePrefix = null;
    }

    public void enableMethodTracer(boolean enableMethodTracer) {
        this.enableMethodTracer = enableMethodTracer;
    }

    public void enableLifecycleTracer(boolean enableLifecycleTracer) {
        this.enableLifecycleTracer = enableLifecycleTracer;
    }

    public void instrumentationRuleFilePath(String instrumentationRuleFilePath) {
        this.instrumentationRuleFilePath = instrumentationRuleFilePath;
    }

    public void instrumentationRuleIncludeClassNamePrefix(List<String> instrumentationRuleIncludeClassNamePrefix) {
        this.instrumentationRuleIncludeClassNamePrefix = instrumentationRuleIncludeClassNamePrefix;
    }

    @Override
    public String toString() {
        return "AndroidGodEyeExtension{" +
                "enableMethodTracer=" + enableMethodTracer +
                ", enableLifecycleTracer=" + enableLifecycleTracer +
                ", instrumentationRuleFilePath='" + instrumentationRuleFilePath + '\'' +
                ", instrumentationRuleIncludeClassNamePrefix=" + instrumentationRuleIncludeClassNamePrefix +
                '}';
    }
}
