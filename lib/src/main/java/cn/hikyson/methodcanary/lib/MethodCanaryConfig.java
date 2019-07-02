package cn.hikyson.methodcanary.lib;

import android.app.Application;

public class MethodCanaryConfig {
    public Application app;
    /**
     * negative means will not write to file
     */
    public int methodEventThreshold;
    public MethodCanaryCallback methodCanaryCallback;


    public static final class MethodCanaryConfigBuilder {
        public Application app;
        public int methodEventThreshold;
        public MethodCanaryCallback methodCanaryCallback;

        private MethodCanaryConfigBuilder() {
        }

        public static MethodCanaryConfigBuilder aMethodCanaryConfig() {
            return new MethodCanaryConfigBuilder();
        }

        public MethodCanaryConfigBuilder app(Application app) {
            this.app = app;
            return this;
        }

        public MethodCanaryConfigBuilder methodEventThreshold(int methodEventThreshold) {
            this.methodEventThreshold = methodEventThreshold;
            return this;
        }

        public MethodCanaryConfigBuilder methodCanaryCallback(MethodCanaryCallback methodCanaryCallback) {
            this.methodCanaryCallback = methodCanaryCallback;
            return this;
        }

        public MethodCanaryConfig build() {
            MethodCanaryConfig methodCanaryConfig = new MethodCanaryConfig();
            methodCanaryConfig.app = this.app;
            methodCanaryConfig.methodCanaryCallback = this.methodCanaryCallback;
            methodCanaryConfig.methodEventThreshold = this.methodEventThreshold;
            return methodCanaryConfig;
        }
    }
}
