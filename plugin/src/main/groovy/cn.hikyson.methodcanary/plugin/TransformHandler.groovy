package cn.hikyson.methodcanary.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

public class TransformHandler {
    public static void handle(Project project, TransformInvocation transformInvocation) {
        Collection<TransformInput> inputs = transformInvocation.inputs
        if (inputs == null) {
            project.logger.quiet("TransformHandler handle: inputs == null")
            return
        }
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (outputProvider != null) {
            outputProvider.deleteAll()
            project.logger.quiet("TransformHandler handle: outputProvider.deleteAll")
        }
        ExcludesEngine excludesEngine = null;
        File methodCanaryJsFile = new File(project.getRootDir(), "MethodCanary.js")
        if (methodCanaryJsFile.exists() && methodCanaryJsFile.isFile()) {
            excludesEngine = new ExcludesEngine(FileUtils.readFileToString(methodCanaryJsFile))
        } else {
            excludesEngine = new ExcludesEngine(null)
        }
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(project, directoryInput, outputProvider)
            }
            input.jarInputs.each { JarInput jarInput ->
                handleJarInputs(project, jarInput, outputProvider)
            }
        }
    }

    static void handleDirectoryInput(Project project, DirectoryInput directoryInput, TransformOutputProvider outputProvider, ExcludesEngine excludesEngine) {
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse { File file ->
                if (file.name.endsWith(".class")) {
                    project.logger.quiet("Dealing with class file [" + file.name + "]")
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new MethodCanaryClassVisitor(project, classWriter, excludesEngine)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(
                            file.parentFile.absolutePath + File.separator + file.name)
                    fos.write(code)
                    fos.close()
                } else {
                    project.logger.quiet("Exclude file [" + file.name + "]")
                }
            }
        }
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    static void handleJarInputs(Project project, JarInput jarInput, TransformOutputProvider outputProvider, ExcludesEngine excludesEngine) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                if (entryName.endsWith(".class")) {
                    project.logger.quiet("Dealing with jar [" + jarName + "], class file [" + entryName + "]")
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new MethodCanaryClassVisitor(project, classWriter, excludesEngine)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    project.logger.quiet("Exclude jar [" + jarName + "], file [" + entryName + "]")
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }
}