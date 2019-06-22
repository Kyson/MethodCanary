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

        if (id != that.id) return false;
        if (priority != that.priority) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + priority;
        return result;
    }
}
