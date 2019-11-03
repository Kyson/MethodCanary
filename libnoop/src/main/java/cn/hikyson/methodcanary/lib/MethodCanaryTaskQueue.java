package cn.hikyson.methodcanary.lib;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MethodCanaryTaskQueue {
    private BlockingQueue<Runnable> mTaskQueue;
    private MethodCanaryTaskDispatcher mDispatcher;

    MethodCanaryTaskQueue() {
        mTaskQueue = new LinkedBlockingQueue<>();
    }

    void addTask(Runnable runnable) {
        mTaskQueue.offer(runnable);
    }

    Runnable takeNextTask() throws InterruptedException {
        return mTaskQueue.take();
    }

    boolean isEmpty() {
        return mTaskQueue.isEmpty();
    }

    synchronized void start() {
        if (mDispatcher == null) {
            mDispatcher = new MethodCanaryTaskDispatcher(this);
            mDispatcher.setName("MethodCanary-Record");
            mDispatcher.start();
        }
    }

    synchronized void stop() {
        if (mDispatcher != null) {
            mDispatcher.quit();
            mDispatcher = null;
        }
    }
}
