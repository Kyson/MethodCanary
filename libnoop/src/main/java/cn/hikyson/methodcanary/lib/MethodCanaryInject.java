package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;

/**
 * 参数协议见class: MethodEventInjectProtocol
 */
@Keep
public class MethodCanaryInject {

    @Keep
    public static void onMethodEnter(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {

    }

    @Keep
    public static void onMethodExit(final int accessFlag, final String className, final String methodName, final String desc, int type, Object[] objs) {

    }
}
