package cn.hikyson.methodcanary.lib;

public class MethodCanaryConfig {
    /**
     * millis
     */
    public long lowCostThresholdTimeMillis;

    public MethodCanaryConfig(long lowCostThresholdTimeMillis) {
        this.lowCostThresholdTimeMillis = lowCostThresholdTimeMillis;
    }
}
