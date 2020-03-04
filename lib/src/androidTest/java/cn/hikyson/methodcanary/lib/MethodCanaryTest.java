package cn.hikyson.methodcanary.lib;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class MethodCanaryTest {
    private Map<ThreadInfo, List<MethodEvent>> record1;
    private Map<ThreadInfo, List<MethodEvent>> record2;

    @Test
    public void start() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MethodCanary.get().startMethodTracing("1");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MethodCanary.get().startMethodTracing("2");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MethodCanary.get().stopMethodTracing("1", new MethodCanaryConfig(0), new MethodCanaryOnGetRecordsCallback() {
                    @Override
                    public void onGetRecords(String sessionTag, long startMillis, long stopMillis, Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                        record1 = methodEventMap;
                        countDownLatch.countDown();
                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MethodCanary.get().stopMethodTracing("2", new MethodCanaryConfig(0), new MethodCanaryOnGetRecordsCallback() {
                    @Override
                    public void onGetRecords(String sessionTag, long startMillis, long stopMillis, Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                        record2 = methodEventMap;
                        countDownLatch.countDown();
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i % 2 == 0) {
                        MethodCanaryInject.onMethodExit(i, "class" + i, "method" + i, "desc" + i, 0, null);
                    } else {
                        MethodCanaryInject.onMethodEnter(i, "class" + i, "method" + i, "desc" + i, 0, null);
                    }
                }
            }
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stop() {
    }
}