package cn.hikyson.methodcanary.lib;

import android.app.Application;

public class MethodCanaryConfig {
    public Application app;
    public int methodEventThreshold;
    public MethodCanaryOutputCallback methodCanaryOutputCallback;

    public static final class MethodCanaryConfigBuilder {
        public Application app;
        public int methodEventThreshold;
        public MethodCanaryOutputCallback methodCanaryOutputCallback;

        private MethodCanaryConfigBuilder() {
        }

        public static MethodCanaryConfigBuilder aMethodCanaryConfig() {
            return new MethodCanaryConfigBuilder();
        }

        public MethodCanaryConfigBuilder app(Application app) {
            this.app = app;
            return this;
        }

        public MethodCanaryConfigBuilder methodEventThreshold(int ethodEventThreshold) {
            this.methodEventThreshold = ethodEventThreshold;
            return this;
        }

        public MethodCanaryConfigBuilder methodCanaryOutputCallback(MethodCanaryOutputCallback ethodCanaryOutputCallback) {
            this.methodCanaryOutputCallback = ethodCanaryOutputCallback;
            return this;
        }

        public MethodCanaryConfig build() {
            MethodCanaryConfig methodCanaryConfig = new MethodCanaryConfig();
            methodCanaryConfig.methodEventThreshold = this.methodEventThreshold;
            methodCanaryConfig.methodCanaryOutputCallback = this.methodCanaryOutputCallback;
            methodCanaryConfig.app = this.app;
            return methodCanaryConfig;
        }
    }
}
