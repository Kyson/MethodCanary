package cn.hikyson.methodcanary.plugin

import org.gradle.api.Project
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

public class MethodCanaryClassVisitor extends ClassVisitor {
    private String mClassName
    private Project mProject
    private ExcludesEngine mExcludesEngine

    public MethodCanaryClassVisitor(Project project, ClassVisitor cv, ExcludesEngine excludesEngine) {
        super(Opcodes.ASM5, cv)
        this.mProject = project
        this.mExcludesEngine = excludesEngine
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        mClassName = name
        this.mProject.logger.quiet("MethodCanaryClassVisitor visit class start [" + mClassName + "]")
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        this.mProject.logger.quiet("MethodCanaryClassVisitor visit class [" + mClassName + "], visitMethod [" + name + "]")
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        methodVisitor = new MethodCanaryMethodVisitor(this.mProject, methodVisitor, access, mClassName, name, desc)
        return methodVisitor
    }

    @Override
    public void visitEnd() {
        super.visitEnd()
        this.mProject.logger.quiet("MethodCanaryClassVisitor visit class end [" + mClassName + "]")
    }

    public static class MethodCanaryMethodVisitor extends AdviceAdapter {
        private Project mProject
        private int mAccess
        private String mClassName
        private String mName
        private String mDesc

        MethodCanaryMethodVisitor(Project project, MethodVisitor mv, int access, String className, String name, String desc) {
            super(Opcodes.ASM5, mv, access, name, desc)
            this.mProject = project
            this.mAccess = access
            this.mClassName = className
            this.mName = name
            this.mDesc = desc
        }

        @Override
        protected void onMethodEnter() {
            this.mProject.logger.quiet("MethodCanaryMethodVisitor onMethodEnter start: class [" + this.mClassName + "], method [" + this.mName + "]")
            mv.visitIntInsn(Opcodes.BIPUSH, this.mAccess);
            mv.visitLdcInsn(this.mClassName);
            mv.visitLdcInsn(this.mName);
            mv.visitLdcInsn(this.mDesc);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodEnter", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
            this.mProject.logger.quiet("MethodCanaryMethodVisitor onMethodEnter end: class [" + this.mClassName + "], method [" + this.mName + "]")
        }

        @Override
        protected void onMethodExit(int i) {
            this.mProject.logger.quiet("MethodCanaryMethodVisitor onMethodExit start: class [" + this.mClassName + "], method [" + this.mName + "]")
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodExit", "()V", false)
            this.mProject.logger.quiet("MethodCanaryMethodVisitor onMethodExit end: class [" + this.mClassName + "], method [" + this.mName + "]")
        }
    }
}
