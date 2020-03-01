package cn.hikyson.methodcanary.plugin;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;

public class ClassHelper {
    private static MethodInfoForLifecycle ON_ACTIVITY_CREATE = new MethodInfoForLifecycle("onCreate", "(Landroid/os/Bundle;)V");
    private static MethodInfoForLifecycle ON_ACTIVITY_START = new MethodInfoForLifecycle("onStart", "()V");
    private static MethodInfoForLifecycle ON_ACTIVITY_RESUME = new MethodInfoForLifecycle("onResume", "()V");
    private static MethodInfoForLifecycle ON_ACTIVITY_PAUSE = new MethodInfoForLifecycle("onPause", "()V");
    private static MethodInfoForLifecycle ON_ACTIVITY_STOP = new MethodInfoForLifecycle("onStop", "()V");
    private static MethodInfoForLifecycle ON_ACTIVITY_SAVE_INSTANCE_STATE = new MethodInfoForLifecycle("onSaveInstanceState", "(Landroid/os/Bundle;)V");
    private static MethodInfoForLifecycle ON_ACTIVITY_DESTORY = new MethodInfoForLifecycle("onDestroy", "()V");
    private static MethodInfoForLifecycle ON_FRAGMENT_ATTACH = new MethodInfoForLifecycle("onAttach", "(Landroid/app/Activity;)V");
    private static MethodInfoForLifecycle ON_FRAGMENT_CREATE = new MethodInfoForLifecycle("onCreate", "(Landroid/os/Bundle;)V");
    private static MethodInfoForLifecycle ON_FRAGMENT_VIEW_CREATE = new MethodInfoForLifecycle("onCreateView", "(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;");
    private static MethodInfoForLifecycle ON_FRAGMENT_START = new MethodInfoForLifecycle("onStart", "()V");
    private static MethodInfoForLifecycle ON_FRAGMENT_RESUME = new MethodInfoForLifecycle("onResume", "()V");
    private static MethodInfoForLifecycle ON_FRAGMENT_PAUSE = new MethodInfoForLifecycle("onPause", "()V");
    private static MethodInfoForLifecycle ON_FRAGMENT_STOP = new MethodInfoForLifecycle("onStop", "()V");
    private static MethodInfoForLifecycle ON_FRAGMENT_SAVE_INSTANCE_STATE = new MethodInfoForLifecycle("onSaveInstanceState", "(Landroid/os/Bundle;)V");
    private static MethodInfoForLifecycle ON_FRAGMENT_VIEW_DESTROY = new MethodInfoForLifecycle("onDestroyView", "()V");
    private static MethodInfoForLifecycle ON_FRAGMENT_DESTROY = new MethodInfoForLifecycle("onDestory", "()V");
    private static MethodInfoForLifecycle ON_FRAGMENT_DETACH = new MethodInfoForLifecycle("onDetach", "()V");

    private static Set<MethodInfoForLifecycle> LIFECYCLE_EVENTS = new HashSet<>();

    static {
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_CREATE);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_START);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_RESUME);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_PAUSE);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_STOP);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_SAVE_INSTANCE_STATE);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_DESTORY);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_ATTACH);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_CREATE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_VIEW_CREATE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_START);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_RESUME);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_PAUSE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_STOP);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_SAVE_INSTANCE_STATE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_VIEW_DESTROY);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_DESTROY);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_DETACH);
    }

    public static int injectMethodEnter(MethodCanaryClassVisitor.MethodCanaryMethodVisitor methodCanaryMethodVisitor, MethodVisitor mv) {
        if (isLifecycleMethod(methodCanaryMethodVisitor, mv)) {
            if (methodCanaryMethodVisitor.mAndroidGodEyeExtension.enableLifecycleTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.mMethodInfo.access);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mClassInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.desc);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
                mv.visitInsn(Opcodes.DUP);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.AASTORE);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodEnter", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V", false);
                return 1;
            } else {
                return -1;
            }
        } else {
            if (methodCanaryMethodVisitor.mAndroidGodEyeExtension.enableMethodTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.mMethodInfo.access);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mClassInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.desc);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodEnter", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V", false);
                return 0;
            } else {
                return -1;
            }
        }
    }

    public static int injectMethodExit(MethodCanaryClassVisitor.MethodCanaryMethodVisitor methodCanaryMethodVisitor, MethodVisitor mv) {
        if (isLifecycleMethod(methodCanaryMethodVisitor, mv)) {
            if (methodCanaryMethodVisitor.mAndroidGodEyeExtension.enableLifecycleTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.mMethodInfo.access);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mClassInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.desc);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
                mv.visitInsn(Opcodes.DUP);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.AASTORE);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodExit", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V", false);
                return 1;
            } else {
                return -1;
            }
        } else {
            if (methodCanaryMethodVisitor.mAndroidGodEyeExtension.enableMethodTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.mMethodInfo.access);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mClassInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.name);
                mv.visitLdcInsn(methodCanaryMethodVisitor.mMethodInfo.desc);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodExit", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V", false);
                return 0;
            } else {
                return -1;
            }
        }
    }

    private static boolean isLifecycleMethod(MethodCanaryClassVisitor.MethodCanaryMethodVisitor methodCanaryMethodVisitor, MethodVisitor mv) {
        if (isSuperClassObject(methodCanaryMethodVisitor.mClassInfo)) {
            return false;
        }
        if ((methodCanaryMethodVisitor.mMethodInfo.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
            return false;
        }
        if ((methodCanaryMethodVisitor.mMethodInfo.access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
            return false;
        }
        return LIFECYCLE_EVENTS.contains(new MethodInfoForLifecycle(methodCanaryMethodVisitor.mMethodInfo));
    }

    private static boolean isSuperClassObject(ClassInfo mClassInfo) {
        return "java/lang/Object".equals(mClassInfo.name) || "java/lang/Object".equals(mClassInfo.superName);
    }

    private static class MethodInfoForLifecycle {
        String name;
        String desc;

        MethodInfoForLifecycle(MethodInfo methodInfo) {
            this.name = methodInfo.name;
            this.desc = methodInfo.desc;
        }

        MethodInfoForLifecycle(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodInfoForLifecycle that = (MethodInfoForLifecycle) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            return desc != null ? desc.equals(that.desc) : that.desc == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (desc != null ? desc.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MethodInfoForLifecycle{" +
                    "name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}
