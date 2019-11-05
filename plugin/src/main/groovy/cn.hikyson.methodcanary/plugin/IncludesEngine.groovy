package cn.hikyson.methodcanary.plugin

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class IncludesEngine implements IInExcludes {
    private ScriptEngine mJsEngine
    private InternalExcludes mInternalExcludes

    IncludesEngine(InternalExcludes internalExcludes, String eval) {
        if (eval != null && eval.length() > 0) {
            this.mJsEngine = new ScriptEngineManager().getEngineByName("javascript");
            this.mJsEngine.eval(eval)
        }
        this.mInternalExcludes = internalExcludes
    }

    @Override
    boolean isMethodInclude(ClassInfo classInfo, MethodInfo methodInfo) {
        if (this.mInternalExcludes != null && this.mInternalExcludes.isMethodExclude(classInfo, methodInfo)) {
            //内部需要exclude
            return false
        }
        if (mJsEngine == null) {
            return true
        }
        boolean isInclude = ((Invocable) mJsEngine).invokeFunction("isInclude", classInfo, methodInfo)
        return isInclude
    }
}