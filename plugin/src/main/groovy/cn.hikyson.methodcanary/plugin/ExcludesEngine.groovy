package cn.hikyson.methodcanary.plugin

import org.apache.http.util.TextUtils

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

public class ExcludesEngine implements lExcludes {
    private ScriptEngine mJsEngine

    ExcludesEngine(String eval) {
        if (!TextUtils.isEmpty(eval)) {
            this.mJsEngine = new ScriptEngineManager().getEngineByName("javascript");
            this.mJsEngine.eval(eval)
        }
    }

    public boolean isMethodExclude(ClassInfo classInfo, MethodInfo methodInfo) {
        if (mJsEngine == null) {
            return false
        }
        boolean isExclude = ((Invocable) mJsEngine).invokeFunction("isExclude", classInfo, methodInfo)
        return isExclude
    }
}