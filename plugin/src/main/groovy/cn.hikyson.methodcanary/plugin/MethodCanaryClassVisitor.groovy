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
        public Project mProject
        public ClassInfo mClassInfo
        public MethodInfo mMethodInfo
        public IncludesEngine mIncludesEngine
        public StringBuilder mResult

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
            int type = ClassHelper.injectMethodEnter(this, mv)
            this.mResult.append("PU: class [" + this.mClassInfo.name + "], method [" + String.valueOf(this.mMethodInfo) + "], type [" + MethodEventInjectProtocol.Type.toString(type) + "]").append("\n")
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodEnter end: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
        }

        @Override
        protected void onMethodExit(int i) {
            if (!this.mIncludesEngine.isMethodInclude(this.mClassInfo, this.mMethodInfo)) {
//                this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit [EXCLUDE]: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
                return
            }
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit start: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
            int type = ClassHelper.injectMethodExit(this, mv)
            this.mResult.append("PO: class [" + this.mClassInfo.name + "], method [" + String.valueOf(this.mMethodInfo) + "], type [" + MethodEventInjectProtocol.Type.toString(type) + "]").append("\n")
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit end: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
        }
    }
}
