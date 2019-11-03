package cn.hikyson.methodcanary.lib;

public class MethodCanaryConfig {
    /**
     * nano time
     */
    public long lowCostThresholdNanoTime;

    public MethodCanaryConfig(long lowCostThresholdNanoTime) {
        this.lowCostThresholdNanoTime = lowCostThresholdNanoTime;
    }
}
