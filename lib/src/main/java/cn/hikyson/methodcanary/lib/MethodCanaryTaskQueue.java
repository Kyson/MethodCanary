package cn.hikyson.methodcanary.lib;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MethodCanaryTaskQueue {
    private BlockingQueue<Runnable> mTaskQueue;
    private MethodCanaryTaskDispatcher mDispatcher;
    private String mName;

    MethodCanaryTaskQueue(String name) {
        mTaskQueue = new LinkedBlockingQueue<>();
        mName = name;
    }

    void queueTask(Runnable runnable) {
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
            mDispatcher.setName(this.mName);
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
