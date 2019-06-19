package cn.hikyson.methodcanary.plugin

import org.apache.http.util.TextUtils

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class InExcludesEngine implements IInExcludes {
    private ScriptEngine mJsEngine
    private InternalExcludes mInternalExcludes

    InExcludesEngine(InternalExcludes internalExcludes, String eval) {
        if (!TextUtils.isEmpty(eval)) {
            this.mJsEngine = new ScriptEngineManager().getEngineByName("javascript");
            this.mJsEngine.eval(eval)
        }
        this.mInternalExcludes = internalExcludes
    }

    @Override
    boolean isMethodInclude(ClassInfo classInfo, MethodInfo methodInfo) {
        if (mJsEngine == null) {
            return false
        }
        boolean isInclude = ((Invocable) mJsEngine).invokeFunction("isInclude", classInfo, methodInfo)
        return isInclude
    }

    @Override
    boolean isMethodExclude(ClassInfo classInfo, MethodInfo methodInfo) {
        if (this.mInternalExcludes != null && this.mInternalExcludes.isMethodExclude(classInfo, methodInfo)) {
            //内部需要exclude
            return true
        }
        if (mJsEngine == null) {
            return false
        }
        boolean isExclude = ((Invocable) mJsEngine).invokeFunction("isExclude", classInfo, methodInfo)
        return isExclude
    }
}