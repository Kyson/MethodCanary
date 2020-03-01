package cn.hikyson.methodcanary.lib;

import android.os.Looper;

import java.util.Stack;

class MethodCanaryForSpecial {
    private Stack<MethodEvent> mMethodEventStackMap = new Stack<>();

    void setOnPageLifecycleEventCallback(OnPageLifecycleEventCallback onPageLifecycleEventCallback) {
        mOnPageLifecycleEventCallback = onPageLifecycleEventCallback;
    }

    private OnPageLifecycleEventCallback mOnPageLifecycleEventCallback;

    MethodCanaryForSpecial() {
    }

    void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            MethodCanaryLogger.log(String.format("MethodCanary [%s].[%s] is lifecycle event and must run in main thread!", className, methodName));
        }
        mMethodEventStackMap.push(new MethodEvent(className, accessFlag, methodName, desc, true, System.nanoTime()));
//        Log.d("AndroidGodEye", String.format("[AndroidGodEye] MethodCanary [%s].[%s] start.", className, methodName));
    }

    void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            MethodCanaryLogger.log(String.format("MethodCanary [%s].[%s] is lifecycle event and must run in main thread!", className, methodName));
        }
        MethodEvent methodExitEvent = new MethodEvent(className, accessFlag, methodName, desc, false, System.nanoTime());
        if (!mMethodEventStackMap.isEmpty()) {
            MethodEvent methodEnterEvent = mMethodEventStackMap.pop();
            if (methodEnterEvent != null) {
                methodExitEvent.pairMethodEvent = methodEnterEvent;
                methodEnterEvent.pairMethodEvent = methodExitEvent;
            }
        }
//        Log.d("AndroidGodEye", String.format("[AndroidGodEye] MethodCanary [%s].[%s] stop.", className, methodName));
        if (mOnPageLifecycleEventCallback != null) {
            mOnPageLifecycleEventCallback.onLifecycleEvent(methodExitEvent, objs[0]);
        }
    }
}
