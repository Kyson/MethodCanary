package cn.hikyson.methodcanary.plugin;

import java.util.HashSet;
import java.util.Set;

public class ClassHelper {
//    MethodCanaryInject.onMethodEnter(4, "cn/hikyson/methodcanary/sample/Main2Activity", "onCreate", "(Landroid/os/Bundle;)V");

    static String ON_CREATE = "onCreate";
    static String ON_START = "onStart";
    static String ON_RESUME = "onResume";
    static String ON_PAUSE = "onPause";
    static String ON_STOP = "onStop";
    static String ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";
    static String ON_DESTORY = "onDestroy";





    static Set<String> LIFECYCLE_EVENTS = new HashSet<>();

    static {

    }


//    @Override
//    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    public void onActivityStarted(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityResumed(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityPaused(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityStopped(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//    }
//
//    @Override
//    public void onActivityDestroyed(Activity activity) {
//
//    }
}
