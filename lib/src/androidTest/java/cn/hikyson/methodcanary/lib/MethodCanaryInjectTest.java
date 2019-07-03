package cn.hikyson.methodcanary.lib;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

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
        Context context = InstrumentationRegistry.getTargetContext();
        MethodCanaryInject.install(MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
                .app((Application) context.getApplicationContext())
                .methodEventThreshold(0)
                .methodCanaryCallback(new MethodCanaryCallback() {
                    @Override
                    public void onStopped() {
                        System.out.println("onStopped");
                    }

                    @Override
                    public void outputToMemory(long startTimeNanos, long stopTimeNanos, Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
                        System.out.println("startTimeNanos:" + startTimeNanos);
                        System.out.println("stopTimeNanos:" + stopTimeNanos);
                        System.out.println("methodEventMap:" + methodEventMap);
                        MethodCanaryInject.uninstall();
                    }

                    @Override
                    public void outputToFile(long startTimeNanos, long stopTimeNanos, File methodEventsFile) {
                        System.out.println("startTimeNanos:" + startTimeNanos);
                        System.out.println("stopTimeNanos:" + stopTimeNanos);
                        System.out.println("methodEventMap:" + methodEventsFile.getAbsolutePath());
                        MethodCanaryInject.uninstall();
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
    }

    @Test
    public void stopMonitor() {
    }
}