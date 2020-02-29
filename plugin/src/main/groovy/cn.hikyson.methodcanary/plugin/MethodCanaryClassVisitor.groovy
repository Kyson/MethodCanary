package cn.hikyson.methodcanary.plugin

import org.gradle.api.Project
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

class MethodCanaryClassVisitor extends ClassVisitor {
    private Project mProject
    private IncludesEngine mIncludesEngine
    private ClassInfo mClassInfo
    private StringBuilder mResult
    private ClassReader mClassReader

    MethodCanaryClassVisitor(Project project, ClassReader classReader, ClassVisitor cv, IncludesEngine includesEngine, StringBuilder result) {
        super(Opcodes.ASM5, cv)
        this.mProject = project
        this.mIncludesEngine = includesEngine
        this.mClassInfo = new ClassInfo()
        this.mResult = result
        this.mClassReader = classReader
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.mClassInfo.access = access
        this.mClassInfo.name = name
        this.mClassInfo.superName = superName
        this.mClassInfo.interfaces = interfaces
//        this.mProject.logger.quiet("[MethodCanary] ClassVisitor visit class " + String.valueOf(this.mClassInfo))
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodInfo methodInfo = new MethodInfo(access, name, desc)
//        this.mProject.logger.quiet("[MethodCanary] ClassVisitor visit method " + String.valueOf(methodInfo) + " " + String.valueOf(this.mClassInfo))
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        methodVisitor = new MethodCanaryMethodVisitor(this.mProject, methodVisitor, this.mClassInfo, methodInfo, this.mIncludesEngine, this.mResult)
        return methodVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
    }

    static class MethodCanaryMethodVisitor extends AdviceAdapter {
        private Project mProject
        private ClassInfo mClassInfo
        private MethodInfo mMethodInfo
        private IncludesEngine mIncludesEngine
        private StringBuilder mResult

        MethodCanaryMethodVisitor(Project project, MethodVisitor mv, ClassInfo classInfo, MethodInfo methodInfo, IncludesEngine includesEngine, StringBuilder result) {
            super(Opcodes.ASM5, mv, methodInfo.access, methodInfo.name, methodInfo.desc)
            this.mProject = project
            this.mClassInfo = classInfo
            this.mMethodInfo = methodInfo
            this.mIncludesEngine = includesEngine
            this.mResult = result
        }

        @Override
        protected void onMethodEnter() {
            if (!this.mIncludesEngine.isMethodInclude(this.mClassInfo, this.mMethodInfo)) {
//                this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodEnter [EXCLUDE]: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
                return
            }
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodEnter start: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
            mv.visitIntInsn(Opcodes.BIPUSH, this.mMethodInfo.access)
            mv.visitLdcInsn(this.mClassInfo.name)
            mv.visitLdcInsn(this.mMethodInfo.name)
            mv.visitLdcInsn(this.mMethodInfo.desc)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodEnter", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false)
            this.mResult.append("INJECT class [" + this.mClassInfo.name + "], method [" + String.valueOf(this.mMethodInfo) + "]").append("\n")
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodEnter end: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
        }

        @Override
        protected void onMethodExit(int i) {
            if (!this.mIncludesEngine.isMethodInclude(this.mClassInfo, this.mMethodInfo)) {
//                this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit [EXCLUDE]: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
                return
            }
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit start: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
            mv.visitIntInsn(Opcodes.BIPUSH, this.mMethodInfo.access)
            mv.visitLdcInsn(this.mClassInfo.name)
            mv.visitLdcInsn(this.mMethodInfo.name)
            mv.visitLdcInsn(this.mMethodInfo.desc)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/hikyson/methodcanary/lib/MethodCanaryInject", "onMethodExit", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false)
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit end: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
        }
    }
}
