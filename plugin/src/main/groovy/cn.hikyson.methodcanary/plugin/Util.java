package cn.hikyson.methodcanary.plugin;

import org.gradle.api.GradleException;

public class Util {

    static void throwException(String message) {
        throw new GradleException(String.format("%s\nSubmit issue in [https://github.com/Kyson/AndroidGodEye/issues] if you have any question.", message));
    }
}
