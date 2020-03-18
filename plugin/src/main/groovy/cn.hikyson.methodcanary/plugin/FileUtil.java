package cn.hikyson.methodcanary.plugin;

import org.gradle.api.Project;

import java.io.File;

public class FileUtil {
    static File ensureOutputDir(Project project) {
        File intermediatesDir = new File(project.getBuildDir(), "intermediates");
        File methodCanaryDir = new File(intermediatesDir, "android_god_eye");
        if (!methodCanaryDir.exists()) {
            methodCanaryDir.mkdirs();
        }
        return methodCanaryDir;
    }

    static File outputResult(Project project) {
        return new File(ensureOutputDir(project), "method_canary_instrumentation.txt");
    }

}
