package cn.hikyson.methodcanary.lib;

public interface OnPageLifecycleEventCallback {
    void onLifecycleEvent(MethodEvent lifecycleExitMethodEvent, Object page);
}