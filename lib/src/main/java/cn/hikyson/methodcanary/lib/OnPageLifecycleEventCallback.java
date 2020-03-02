package cn.hikyson.methodcanary.lib;

public interface OnPageLifecycleEventCallback {
    void onLifecycleEvent(MethodEvent lifecycleMethodEvent, Object page);
}