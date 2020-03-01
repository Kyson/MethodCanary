package cn.hikyson.methodcanary.plugin

import org.gradle.api.Project

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.apache.commons.io.FileUtils

class IncludesEngine implements IInExcludes {
    private ScriptEngine mJsEngine
    private InternalExcludes mInternalExcludes
    private ExternalIncludes mExternalIncludes

    IncludesEngine(Project project, AndroidGodEyeExtension androidGodEyeExtension) {
        if (androidGodEyeExtension.instrumentationRuleFilePath != null && "" != androidGodEyeExtension.instrumentationRuleFilePath) {
            File methodCanaryJsFile = new File(project.getRootDir(), androidGodEyeExtension.instrumentationRuleFilePath)
            if (!methodCanaryJsFile.exists() || !methodCanaryJsFile.isFile()) {
                Util.throwException("[AndroidGodEye][MethodCanary] Can not find instrumentationRuleFilePath: " + androidGodEyeExtension.instrumentationRuleFilePath)
            }
            String inExcludeEngineContent = FileUtils.readFileToString(methodCanaryJsFile)
            project.logger.quiet("[AndroidGodEye][MethodCanary] InstrumentationRuleFile:\n" + inExcludeEngineContent)
            this.mJsEngine = new ScriptEngineManager().getEngineByName("javascript");
            this.mJsEngine.eval(inExcludeEngineContent)
        }
        this.mExternalIncludes = new ExternalIncludes(androidGodEyeExtension.instrumentationRuleIncludeClassNamePrefix)
        this.mInternalExcludes = new InternalExcludes()
    }

    @Override
    boolean isMethodInclude(ClassInfo classInfo, MethodInfo methodInfo) {
        if (this.mInternalExcludes != null && this.mInternalExcludes.isMethodExclude(classInfo, methodInfo)) {
            //内部需要exclude
            return false
        }
        if (mJsEngine == null) {
            return mExternalIncludes.isMethodInclude(classInfo, methodInfo)
        }
        boolean isInclude = ((Invocable) mJsEngine).invokeFunction("isInclude", classInfo, methodInfo)
        return isInclude && mExternalIncludes.isMethodInclude(classInfo, methodInfo)
    }
}