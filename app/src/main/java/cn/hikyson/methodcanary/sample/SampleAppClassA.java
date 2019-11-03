package cn.hikyson.methodcanary.sample;


public class SampleAppClassA {
    public static void testMethod1(long cost) {
        try {
            Thread.sleep(cost);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int testMethod2() {
        int j = 0;
        for (int i = 0; i < 10000; i++) {
            j++;
        }
        return j;
    }

    public static String testMethod3() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            s.append(i);
        }
        return s.toString();
    }
}
