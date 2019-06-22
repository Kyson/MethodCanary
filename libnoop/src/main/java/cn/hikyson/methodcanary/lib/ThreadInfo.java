package cn.hikyson.methodcanary.lib;

import java.util.Objects;

public class ThreadInfo {
    public long id;
    public String name;
    public int priority;

    public ThreadInfo(long id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadInfo that = (ThreadInfo) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
