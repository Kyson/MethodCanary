package cn.hikyson.methodcanary.lib;


public class ThreadInfo {
    public long id;
    public String name;
    public int priority;

    public ThreadInfo() {
    }

    public ThreadInfo(long id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    public ThreadInfo copy() {
        return new ThreadInfo(this.id, this.name, this.priority);
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

    @Override
    public String toString() {
        return "[THREAD]" + "id=" + this.id + ";name=" + this.name + ";priority=" + this.priority;
    }
}
