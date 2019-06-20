package cn.hikyson.methodcanary.lib;

import android.support.annotation.Keep;
import com.orhanobut.logger.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Keep
public class MethodCanaryInject {
    private static volatile boolean sStopped = true;
    private static AtomicInteger sTaskRunningCount = new AtomicInteger(0);

    private static Map<ThreadInfo, List<MethodEvent>> sMethodEventMap = new HashMap<>();
    private static ThreadPoolExecutor sSingle = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public interface IOutput {
        void output(Map<ThreadInfo, List<MethodEvent>> methodEventMap);
    }

    @Keep
    public static void onMethodEnter(int accessFlag, String className, String methodName, String desc) {
        if (sStopped) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        final ThreadInfo threadInfo = new ThreadInfo(currentThread.getId(), currentThread.getName(), currentThread.getPriority());
        final MethodEnterEvent methodEnterEvent = new MethodEnterEvent(className, accessFlag, methodName, desc, System.nanoTime());
        sTaskRunningCount.incrementAndGet();
        if (sStopped) {//double check for thread safe
            return;
        }
        sSingle.execute(new Runnable() {
            @Override
            public void run() {
                List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>();
                    sMethodEventMap.put(threadInfo, methodEvents);
                }
                Logger.d("方法进入:" + methodEnterEvent.methodName);
                methodEvents.add(methodEnterEvent);
                sTaskRunningCount.decrementAndGet();
            }
        });
    }

    @Keep
    public static void onMethodExit(int accessFlag, String className, final String methodName, String desc) {
        if (sStopped) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        final ThreadInfo threadInfo = new ThreadInfo(currentThread.getId(), currentThread.getName(), currentThread.getPriority());
        final MethodExitEvent methodExitEvent = new MethodExitEvent(className, accessFlag, methodName, desc, System.nanoTime());
        sTaskRunningCount.incrementAndGet();
        if (sStopped) {//double check for thread safe
            return;
        }
        sSingle.execute(new Runnable() {
            @Override
            public void run() {
                List<MethodEvent> methodEvents = sMethodEventMap.get(threadInfo);
                if (methodEvents == null) {
                    methodEvents = new ArrayList<>();
                    sMethodEventMap.put(threadInfo, methodEvents);
                }
                Logger.d("方法退出:" + methodExitEvent.methodName);
                methodEvents.add(methodExitEvent);
                sTaskRunningCount.decrementAndGet();
            }
        });
    }

    public static void startMonitor() throws Exception {
        if (!sStopped || sTaskRunningCount.get() > 0) {//正在运行中
            Logger.d("开启监控失败");
            throw new Exception("method canary is monitoring, please wait for monitor's stopping.");
        }
        sStopped = false;
        Logger.d("开启监控成功");
    }

    /**
     * record during last start monitor success
     *
     * @param output
     */
    public static void stopMonitorAndOutput(final IOutput output) {
        sStopped = true;
        Logger.d("结束监控中...");
        sSingle.execute(new Runnable() {
            @Override
            public void run() {
                output.output(new HashMap<>(sMethodEventMap));
                sMethodEventMap.clear();
                sTaskRunningCount.set(0);
                Logger.d("监控结束.");
            }
        });
    }
}
