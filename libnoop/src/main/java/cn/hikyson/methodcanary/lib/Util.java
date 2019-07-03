package cn.hikyson.methodcanary.lib;

import java.util.regex.Pattern;

public class Util {
    public static final Pattern PATTERN_THREAD = Pattern.compile("^\\[THREAD]id=(\\d*);name=(.*);priority=(\\d*)$");
    public static final Pattern PATTERN_METHOD_ENTER = Pattern.compile("^PUSH:et=(\\d*);cn=(.*);ma=(-?\\d*);mn=(.*);md=(.*)$");
    public static final Pattern PATTERN_METHOD_EXIT = Pattern.compile("^POP:et=(\\d*);cn=(.*);ma=(-?\\d*);mn=(.*);md=(.*)$");
    public static final String START_THREAD = "[THREAD]";
    public static final String START_METHOD_ENTER = "PUSH:";
    public static final String START_METHOD_EXIT = "POP:";
}
