package cn.hikyson.methodcanary.lib;

public class ThreadInfo {
    public long id;
    public String name;
    public int priority;

    public ThreadInfo(long id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }
}
