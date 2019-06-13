package cn.hikyson.methodcanary.plugin

public class MethodCanaryExtension {

    public List<String> excludeClassNames;

    MethodCanaryExtension() {
        this.excludeClassNames = new ArrayList<>()
    }

    public void setExcludeClassNames(List<String> excludeClassNames) {
        this.excludeClassNames = excludeClassNames
    }
}