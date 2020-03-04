package cn.hikyson.methodcanary.lib;

import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class MethodCanaryForSpecial {
    private Stack<MethodEvent> mMethodEventStackMap = new Stack<>();
    private final Object mLockForOnPageLifecycleEventCallback = new Object();
    private List<OnPageLifecycleEventCallback> mOnPageLifecycleEventCallbacks = new ArrayList<>();

    void addOnPageLifecycleEventCallback(OnPageLifecycleEventCallback onPageLifecycleEventCallback) {
        synchronized (mLockForOnPageLifecycleEventCallback) {
            mOnPageLifecycleEventCallbacks.add(onPageLifecycleEventCallback);
        }
    }

    void removeOnPageLifecycleEventCallback(OnPageLifecycleEventCallback onPageLifecycleEventCallback) {
        synchronized (mLockForOnPageLifecycleEventCallback) {
            mOnPageLifecycleEventCallbacks.remove(onPageLifecycleEventCallback);
        }
    }

    void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (!filterMethod(accessFlag, className, methodName, desc, type, objs)) {
            return;
        }
        MethodEvent methodEnterEvent = new MethodEvent(className, accessFlag, methodName, desc, true, System.currentTimeMillis(), type);
        mMethodEventStackMap.push(methodEnterEvent);
        MethodCanaryLogger.log(String.format("MethodCanary page [%s] lifecycle event [%s] start.", className, methodName));
        notifyOnPageLifecycleEventCallbacks(methodEnterEvent, objs[0]);
    }

    void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (!filterMethod(accessFlag, className, methodName, desc, type, objs)) {
            return;
        }
        MethodEvent methodExitEvent = new MethodEvent(className, accessFlag, methodName, desc, false, System.currentTimeMillis(), type);
        if (!mMethodEventStackMap.isEmpty()) {
            MethodEvent methodEnterEvent = mMethodEventStackMap.pop();
            if (methodEnterEvent != null) {
                methodExitEvent.pairMethodEvent = methodEnterEvent;
                methodEnterEvent.pairMethodEvent = methodExitEvent;
            }
        }
        MethodCanaryLogger.log(String.format("MethodCanary page [%s] lifecycle event [%s] end, cost %sms", className, methodName, (methodExitEvent.pairMethodEvent == null ? 0 : (methodExitEvent.eventTimeMillis - methodExitEvent.pairMethodEvent.eventTimeMillis))));
        notifyOnPageLifecycleEventCallbacks(methodExitEvent, objs[0]);
    }

    private boolean filterMethod(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            MethodCanaryLogger.log(String.format("MethodCanary ignored! [%s].[%s] is lifecycle event and must run in main thread.", className, methodName));
            return false;
        }
        if (!checkLifecycleOwnerIsSelf(className, objs[0])) {
            MethodCanaryLogger.log(String.format("MethodCanary ignored! class [%s] and real page [%s] are not match.", className, objs[0] == null ? "null" : objs[0].getClass().getName()));
            return false;
        }
        return true;
    }

    /**
     * check whether event is sent by page self
     * page parent will also send event which should be exclude
     *
     * @param className
     * @param page
     * @return
     */
    private boolean checkLifecycleOwnerIsSelf(String className, Object page) {
        if (page == null) {
            return false;
        }
        return page.getClass().getName().equals(className.replaceAll("/", "."));
    }

    private void notifyOnPageLifecycleEventCallbacks(MethodEvent lifecycleMethodEvent, Object page) {
        if (mOnPageLifecycleEventCallbacks == null || mOnPageLifecycleEventCallbacks.isEmpty()) {
            return;
        }
        Object[] listeners = mOnPageLifecycleEventCallbacks.toArray();
        if (listeners != null) {
            for (Object o : listeners) {
                ((OnPageLifecycleEventCallback) o).onLifecycleEvent(lifecycleMethodEvent, page);
            }
        }
    }
}
