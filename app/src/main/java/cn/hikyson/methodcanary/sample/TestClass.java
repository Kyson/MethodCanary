package cn.hikyson.methodcanary.sample;

import cn.hikyson.methodcanary.lib.MethodCanaryInject;

public class TestClass {
    public static void testMethod() {
        MethodCanaryInject.onMethodEnter(11, "classA", "methodA", "descA");
    }
}
