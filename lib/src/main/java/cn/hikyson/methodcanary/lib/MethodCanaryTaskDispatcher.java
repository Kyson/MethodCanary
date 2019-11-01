package cn.hikyson.methodcanary.lib;

class MethodCanaryTaskDispatcher extends Thread {

    private MethodCanaryTaskQueue mTaskQueue;
    private volatile boolean mQuit = false;

    MethodCanaryTaskDispatcher(MethodCanaryTaskQueue taskQueue) {
        this.mTaskQueue = taskQueue;
    }

    @Override
    public void run() {
        setPriority(Thread.NORM_PRIORITY);
        while (true) {
            try {
                Runnable task = mTaskQueue.takeNextTask();
                task.run();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
            }
        }
    }

    void quit() {
        mQuit = true;
        interrupt();
    }
}
