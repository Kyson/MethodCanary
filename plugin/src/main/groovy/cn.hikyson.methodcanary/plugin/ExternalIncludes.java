package cn.hikyson.methodcanary.plugin;

import java.util.List;

public class ExternalIncludes {
    private List<String> includeClassNamePrefix;

    public ExternalIncludes(List<String> includeClassNamePrefix) {
        this.includeClassNamePrefix = includeClassNamePrefix;
    }

    boolean isMethodInclude(ClassInfo classInfo, MethodInfo methodInfo) {
        if (includeClassNamePrefix == null) {
            return true;
        }
        for (String prefix : includeClassNamePrefix) {
            if (classInfo.name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
