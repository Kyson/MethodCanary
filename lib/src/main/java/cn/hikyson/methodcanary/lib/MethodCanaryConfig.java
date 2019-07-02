package cn.hikyson.methodcanary.lib;

import android.app.Application;

public class MethodCanaryConfig {
    public Application app;
    /**
     * <=0 说明不需要写文件，直接使用内存
     */
    public int methodEventThreshold;
    public MethodCanaryCallback methodCanaryOutputCallback;

    public static final class MethodCanaryConfigBuilder {
        public Application app;
        public int methodEventThreshold;
        public MethodCanaryCallback methodCanaryOutputCallback;

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

        public MethodCanaryConfigBuilder methodCanaryOutputCallback(MethodCanaryCallback ethodCanaryOutputCallback) {
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
