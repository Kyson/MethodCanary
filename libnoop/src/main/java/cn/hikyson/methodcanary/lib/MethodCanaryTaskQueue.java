package cn.hikyson.methodcanary.lib;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MethodCanaryTaskQueue {
    private BlockingQueue<Runnable> mTaskQueue;
    private final MethodCanaryTaskDispatcher mDispatcher;

    MethodCanaryTaskQueue() {
        mTaskQueue = new LinkedBlockingQueue<>();
        mDispatcher = new MethodCanaryTaskDispatcher(this);
    }

    void addTask(Runnable runnable) {
        mTaskQueue.offer(runnable);
    }

    Runnable takeNextTask() throws InterruptedException {
        return mTaskQueue.take();
    }

    void start() {
        synchronized (mDispatcher) {
            mDispatcher.setName("MethodCanary-Record");
            mDispatcher.start();
        }
    }

    void stop() {
        synchronized (mDispatcher) {
            mDispatcher.quit();
        }
    }
}
