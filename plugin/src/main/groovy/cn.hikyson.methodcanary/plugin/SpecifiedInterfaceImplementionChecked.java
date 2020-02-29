//package cn.hikyson.methodcanary.plugin;
//
//import org.gradle.api.logging.Logger;
//import org.objectweb.asm.ClassReader;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//public class SpecifiedInterfaceImplementionChecked {
//
//    /**
//     * 判断是否实现了指定接口
//     *
//     * @param reader       class reader
//     * @param interfaceSet interface collection
//     * @return check result
//     */
//    public static boolean hasImplSpecifiedInterfaces(ClassReader reader, Set<String> interfaceSet) {
//        if (isObject(reader.getClassName())) {
//            return false;
//        }
//        try {
//            if (containedTargetInterface(reader.getInterfaces(), interfaceSet)) {
//                return true;
//            } else {
//                ClassReader parent = new ClassReader(reader.getSuperName());
//                return hasImplSpecifiedInterfaces(parent, interfaceSet);
//            }
//        } catch (IOException e) {
//            return false;
//        }
//    }
//
//    public static List<String> parentClasses(Logger logger, ClassReader reader) {
//        List<String> parentClassList = new ArrayList<>();
//        parentClasses(logger, reader, parentClassList);
//        return parentClassList;
//    }
//
//    private static void parentClasses(Logger logger, ClassReader reader, List<String> parentClassList) {
//        if (isObject(reader.getClassName())) {
//            return;
//        }
//        final String superName = reader.getSuperName();
//        parentClassList.add(superName);
//        try {
//            logger.quiet(String.format("[MethodCanary] superName class: [%s]", superName));
//            ClassReader parent = new ClassReader(superName);
//            parentClasses(logger, parent, parentClassList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 检查当前类是 Object 类型
//     *
//     * @param className class name
//     * @return checked result
//     */
//    private static boolean isObject(String className) {
//        return "java/lang/Object".equals(className);
//    }
//
//    /**
//     * 检查接口及其父接口是否实现了目标接口
//     *
//     * @param interfaceList 待检查接口
//     * @param interfaceSet  目标接口
//     * @return checked result
//     * @throws IOException exp
//     */
//    private static boolean containedTargetInterface(String[] interfaceList, Set<String> interfaceSet) throws IOException {
//        for (String inter : interfaceList) {
//            if (interfaceSet.contains(inter)) {
//                return true;
//            } else {
//                ClassReader reader = new ClassReader(inter);
//                if (containedTargetInterface(reader.getInterfaces(), interfaceSet)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//}
