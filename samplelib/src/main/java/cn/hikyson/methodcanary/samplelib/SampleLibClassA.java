package cn.hikyson.methodcanary.samplelib;


public class SampleLibClassA {

    private String name;
    private int age;
    private boolean sex;

    public SampleLibClassA(String name, int age, boolean sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public void growup(long cost) throws InterruptedException {
        Thread.sleep(cost);
        this.age = this.age + 1;
    }

    public String callMe() {
        String call = "This is " + name;
        return call;
    }

}
