package cn.hikyson.methodcanary.plugin;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodCanaryClassVisitor extends ClassVisitor {
    private String mClassName;

    public MethodCanaryClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        mClassName = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        methodVisitor = new MethodCanaryMethodVisitor(methodVisitor, access, name, desc, mClassName);
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    public static class MethodCanaryMethodVisitor extends AdviceAdapter {
        private int mAccess;
        private String mClassName;
        private String mName;
        private String mDesc;

        MethodCanaryMethodVisitor(MethodVisitor mv, int access, String className, String name, String desc) {
            super(Opcodes.ASM6, mv, access, name, desc);
            this.mAccess = access;
            this.mClassName = className;
            this.mName = name;
            this.mDesc = desc;
        }

        @Override
        protected void onMethodEnter() {
            mv.visitIntInsn(Opcodes.BIPUSH, this.mAccess);
            mv.visitLdcInsn(this.mClassName);
            mv.visitLdcInsn(this.mName);
            mv.visitLdcInsn(this.mDesc);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodEnter", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
        }

        @Override
        protected void onMethodExit(int i) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodExit", "()V", false);
        }
    }
}
