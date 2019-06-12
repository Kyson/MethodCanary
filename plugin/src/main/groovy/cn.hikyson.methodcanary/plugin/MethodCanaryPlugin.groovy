package cn.hikyson.methodcanary.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class MethodCanaryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.logger.info("MethodCanaryPlugin entry")
        def android = project.extensions.getByType(AppExtension)
        project.logger.info("MethodCanaryPlugin entry2")
        if (android != null) {
            android.registerTransform(new MethodCanaryTransform(project))
            project.logger.info("MethodCanaryPlugin registerTransform")
        }
    }
}
