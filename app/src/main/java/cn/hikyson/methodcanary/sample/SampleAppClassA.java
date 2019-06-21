package cn.hikyson.methodcanary.sample;

import android.util.Log;
import cn.hikyson.methodcanary.lib.MethodCanaryInject;

public class SampleAppClassA {
    public static void testMethod1() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testMethod2() {
        int j = 0;
        for (int i = 0; i < 1000000; i++) {
            j++;
        }
    }

    public static void testMethod3() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int j = 0;
                while (true) {
                    j++;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
