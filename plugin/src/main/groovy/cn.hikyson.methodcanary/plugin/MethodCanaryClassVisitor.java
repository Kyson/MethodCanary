package cn.hikyson.methodcanary.plugin;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodCanaryClassVisitor extends ClassVisitor {
    public MethodCanaryClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        methodVisitor = new MethodCanaryMethodVisitor(methodVisitor, access, name, desc);
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    public static class MethodCanaryMethodVisitor extends AdviceAdapter {

        MethodCanaryMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM6, mv, access, name, desc);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(name, desc, signature, start, end, index);
        }

        @Override
        protected void onMethodEnter() {

        }

        @Override
        protected void onMethodExit(int i) {

        }
    }
}
