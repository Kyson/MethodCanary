package cn.hikyson.methodcanary.lib;

public class MethodCanaryConfig {
    /**
     * nano time
     */
    public long lowCostThreshold;
    public MethodCanaryCallback methodCanaryCallback;

    public static final class MethodCanaryConfigBuilder {
        public long lowCostThreshold;
        public MethodCanaryCallback methodCanaryCallback;

        private MethodCanaryConfigBuilder() {
        }

        public static MethodCanaryConfigBuilder aMethodCanaryConfig() {
            return new MethodCanaryConfigBuilder();
        }

        public MethodCanaryConfigBuilder lowCostThreshold(long lowCostThreshold) {
            this.lowCostThreshold = lowCostThreshold;
            return this;
        }

        public MethodCanaryConfigBuilder methodCanaryCallback(MethodCanaryCallback methodCanaryCallback) {
            this.methodCanaryCallback = methodCanaryCallback;
            return this;
        }

        public MethodCanaryConfig build() {
            MethodCanaryConfig methodCanaryConfig = new MethodCanaryConfig();
            methodCanaryConfig.lowCostThreshold = this.lowCostThreshold;
            methodCanaryConfig.methodCanaryCallback = this.methodCanaryCallback;
            return methodCanaryConfig;
        }
    }
}
