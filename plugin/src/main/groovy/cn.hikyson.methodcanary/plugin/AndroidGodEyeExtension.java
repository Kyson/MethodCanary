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

    boolean isEnable() {
        return enableMethodTracer || enableLifecycleTracer;
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
