package cn.hikyson.methodcanary.plugin;

/**
 * MethodCanaryInject方法协议
 * type {@link Type}
 * objs:
 * TYPE_COMMON: null
 * TYPE_LIFECYCLE: this
 * <p>
 * eg.
 * // common method
 * MethodCanaryInject.onMethodEnter(88, "className", "methodName", "desc", 0, null);
 * // lifecycle method
 * MethodCanaryInject.onMethodEnter(88, "className", "methodName", "desc", 1, new Object[]{this});
 */
public class MethodEventInjectProtocol {

    public static class Type {
        public static final int TYPE_COMMON = 0;
        public static final int TYPE_LIFECYCLE = 1;

        public static String toString(int type) {
            if (type == 0) {
                return "Common";
            } else if (type == 1) {
                return "Lifecycle";
            } else {
                return "Unknown";
            }
        }
    }
}
