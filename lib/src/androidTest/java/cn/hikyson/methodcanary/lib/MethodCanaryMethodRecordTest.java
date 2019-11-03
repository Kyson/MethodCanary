package cn.hikyson.methodcanary.lib;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class MethodCanaryMethodRecordTest {
    private Map<ThreadInfo, List<MethodEvent>> smethodEventMap;

    @Test
    public void getRecords() {
        MethodCanaryTaskQueue methodCanaryTaskQueue = new MethodCanaryTaskQueue();
        methodCanaryTaskQueue.start();
        MethodCanaryMethodRecord methodCanaryMethodRecord = new MethodCanaryMethodRecord(methodCanaryTaskQueue);
        MethodCanary.get().methodCanaryMethodRecord = methodCanaryMethodRecord;
        methodCanaryMethodRecord.startRecordRightNow();
        long startNano = System.nanoTime();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                MethodCanaryInject.onMethodExit(i, "class" + i, "method" + i, "desc" + i);
            } else {
                MethodCanaryInject.onMethodEnter(i, "class" + i, "method" + i, "desc" + i);
            }
        }
        methodCanaryMethodRecord.stopRecordRightNow();
        long stopNano = System.nanoTime();
        for (int i = 20; i < 40; i++) {
            if (i % 2 == 0) {
                MethodCanaryInject.onMethodExit(i, "class" + i, "method" + i, "desc" + i);
            } else {
                MethodCanaryInject.onMethodEnter(i, "class" + i, "method" + i, "desc" + i);
            }
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        methodCanaryMethodRecord.getRecords(startNano, stopNano, new MethodCanaryConfig(0), new MethodCanaryMethodRecord.OnGetRecordsCallback() {
            @Override
            public void onGetRecords(Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                smethodEventMap = methodEventMap;
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, smethodEventMap.size());
    }
}