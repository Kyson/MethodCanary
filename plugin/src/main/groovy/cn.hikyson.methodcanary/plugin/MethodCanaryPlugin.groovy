package cn.hikyson.methodcanary.plugin

import com.android.build.gradle.AppPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MethodCanaryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.logger.quiet("[AndroidGodEye][MethodCanary] Plugin applied.")
        project.extensions.create("AndroidGodEye", AndroidGodEyeExtension.class)
        try {
            if (!project.plugins.hasPlugin(AppPlugin)) {
                Util.throwException('[AndroidGodEye][MethodCanary] Plugin: Android Application plugin [com.android.application] required.')
            }
            def android = project.extensions.android
            if (android != null) {
                android.registerTransform(new MethodCanaryTransform(project))
            } else {
                Util.throwException('[AndroidGodEye][MethodCanary] Extension "android" can not be found.')
            }
        } catch (Throwable e) {
            Util.throwException(String.valueOf(e))
        }
    }
}
