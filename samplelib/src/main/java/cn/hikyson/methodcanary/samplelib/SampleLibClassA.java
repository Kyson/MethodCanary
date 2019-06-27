package cn.hikyson.methodcanary.samplelib;

import android.util.Log;

public class SampleLibClassA {

    private String name;
    private int age;
    private boolean sex;

    public SampleLibClassA(String name, int age, boolean sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public void growup() throws InterruptedException {
        Thread.sleep(1000);
        this.age = this.age + 1;
    }

    public void callMe() {
        String name2 = name;
    }

}
