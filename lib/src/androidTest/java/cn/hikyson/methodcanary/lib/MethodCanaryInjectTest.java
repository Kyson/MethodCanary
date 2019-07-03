package cn.hikyson.methodcanary.lib;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class MethodCanaryInjectTest {

    @Test
    public void install() {
    }

    @Test
    public void uninstall() {
    }

    @Test
    public void onMethodEnter() {
    }

    @Test
    public void onMethodExit() {
    }

    @Test
    public void startMonitor() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MethodCanaryInject.install(MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
                .lowCostThreshold(0)
                .methodCanaryCallback(new MethodCanaryCallback() {
                    @Override
                    public void onStopped(long startTimeNanos, long stopTimeNanos) {
                        System.out.println("onStopped");
                    }

                    @Override
                    public void outputToMemory(long startTimeNanos, long stopTimeNanos, Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                        System.out.println("startTimeNanos:" + startTimeNanos);
                        System.out.println("stopTimeNanos:" + stopTimeNanos);
                        System.out.println("methodEventMap:" + methodEventMap);
                        MethodCanaryInject.uninstall();
                        countDownLatch.countDown();
                    }
                }).build());
        MethodCanaryInject.startMonitor();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                MethodCanaryInject.onMethodExit(i, "class" + i, "method" + i, "desc" + i);
            } else {
                MethodCanaryInject.onMethodEnter(i, "class" + i, "method" + i, "desc" + i);
            }
        }
        MethodCanaryInject.stopMonitor();
        countDownLatch.await();
    }

    @Test
    public void startMonitor2() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MethodCanaryInject.install(MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
                .lowCostThreshold(0)
                .methodCanaryCallback(new MethodCanaryCallback() {
                    @Override
                    public void onStopped(long startTimeNanos, long stopTimeNanos) {
                        System.out.println("onStopped");
                    }

                    @Override
                    public void outputToMemory(long startTimeNanos, long stopTimeNanos, Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                        System.out.println("startTimeNanos:" + startTimeNanos);
                        System.out.println("stopTimeNanos:" + stopTimeNanos);
                        System.out.println("methodEventMap:" + methodEventMap);
                        MethodCanaryInject.uninstall();
                        countDownLatch.countDown();
                    }
                }).build());
        MethodCanaryInject.startMonitor();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                MethodCanaryInject.onMethodExit(i, "class" + i, "method" + i, "desc" + i);
            } else {
                MethodCanaryInject.onMethodEnter(i, "class" + i, "method" + i, "desc" + i);
            }
        }
        MethodCanaryInject.stopMonitor();
        countDownLatch.await();
    }

    @Test
    public void startMonitor3() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MethodCanaryInject.install(MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
                .lowCostThreshold(0)
                .methodCanaryCallback(new MethodCanaryCallback() {
                    @Override
                    public void onStopped(long startTimeNanos, long stopTimeNanos) {
                        System.out.println("onStopped");
                    }

                    @Override
                    public void outputToMemory(long startTimeNanos, long stopTimeNanos, Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                        System.out.println("startTimeNanos:" + startTimeNanos);
                        System.out.println("stopTimeNanos:" + stopTimeNanos);
                        System.out.println("methodEventMap:" + methodEventMap);
                        MethodCanaryInject.uninstall();
                        countDownLatch.countDown();
                    }
                }).build());
        MethodCanaryInject.startMonitor();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                MethodCanaryInject.onMethodExit(i, "class" + i, "method" + i, "desc" + i);
                MethodCanaryInject.onMethodExit(i, "classx" + i, "methodx" + i, "descx" + i);
                MethodCanaryInject.onMethodExit(i, "classy" + i, "methody" + i, "descy" + i);
            } else {
                MethodCanaryInject.onMethodEnter(i, "class" + i, "method" + i, "desc" + i);
            }
        }
        MethodCanaryInject.stopMonitor();
        countDownLatch.await();
    }

    @Test
    public void startMonitor4() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MethodCanaryInject.install(MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
                .lowCostThreshold(0)
                .methodCanaryCallback(new MethodCanaryCallback() {
                    @Override
                    public void onStopped(long startTimeNanos, long stopTimeNanos) {
                        System.out.println("onStopped");
                    }

                    @Override
                    public void outputToMemory(long startTimeNanos, long stopTimeNanos, Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                        System.out.println("startTimeNanos:" + startTimeNanos);
                        System.out.println("stopTimeNanos:" + stopTimeNanos);
                        System.out.println("methodEventMap:" + methodEventMap);
                        MethodCanaryInject.uninstall();
                        countDownLatch.countDown();
                    }
                }).build());
        MethodCanaryInject.startMonitor();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                MethodCanaryInject.onMethodExit(i, "class" + i, "method" + i, "desc" + i);
            } else {
                MethodCanaryInject.onMethodEnter(i, "class" + i, "method" + i, "desc" + i);
                MethodCanaryInject.onMethodEnter(i, "classx" + i, "methodx" + i, "descx" + i);
                MethodCanaryInject.onMethodEnter(i, "classy" + i, "methody" + i, "descy" + i);
            }
        }
        MethodCanaryInject.stopMonitor();
        countDownLatch.await();
    }

    @Test
    public void stopMonitor() {
    }
}