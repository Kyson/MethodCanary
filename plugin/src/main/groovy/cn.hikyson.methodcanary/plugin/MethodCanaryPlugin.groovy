package cn.hikyson.methodcanary.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class MethodCanaryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        if (android != null) {
            android.registerTransform(new MethodCanaryTransform(project))
        }
    }
}
