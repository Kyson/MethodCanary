package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

/**
 * 参数协议见class: MethodEventInjectProtocol
 */
@Keep
public class MethodCanaryInject {

    @Keep
    public static void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (type == 1 && objs != null && objs[0] != null && Util.isPageType(objs[0])) {
            MethodCanary.get().methodCanaryForSpecial.onMethodEnter(accessFlag, className, methodName, desc, type, objs);
        }
        MethodCanary.get().methodCanaryForCommon.onMethodEnter(accessFlag, className, methodName, desc);
    }

    @Keep
    public static void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {
        if (type == 1 && objs != null && objs[0] != null && Util.isPageType(objs[0])) {
            MethodCanary.get().methodCanaryForSpecial.onMethodExit(accessFlag, className, methodName, desc, type, objs);
        }
        MethodCanary.get().methodCanaryForCommon.onMethodExit(accessFlag, className, methodName, desc);
    }
}
