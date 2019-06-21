package cn.hikyson.methodcanary.lib;

import android.app.Application;

public class MethodCanaryConfig {
    // TODO KYSON IMPL 提高性能，开放访问
    private Application mApp;
    private int mMethodEventThreshold;
    private MethodCanaryOutputCallback mMethodCanaryOutputCallback;

    public Application getApp() {
        return mApp;
    }

    public int getMethodEventThreshold() {
        return mMethodEventThreshold;
    }

    public MethodCanaryOutputCallback getMethodCanaryOutputCallback() {
        return mMethodCanaryOutputCallback;
    }

    public static final class MethodCanaryConfigBuilder {
        private Application mApp;
        private int mMethodEventThreshold;
        private MethodCanaryOutputCallback mMethodCanaryOutputCallback;

        private MethodCanaryConfigBuilder() {
        }

        public static MethodCanaryConfigBuilder aMethodCanaryConfig() {
            return new MethodCanaryConfigBuilder();
        }

        public MethodCanaryConfigBuilder app(Application App) {
            this.mApp = App;
            return this;
        }

        public MethodCanaryConfigBuilder methodEventThreshold(int MethodEventThreshold) {
            this.mMethodEventThreshold = MethodEventThreshold;
            return this;
        }

        public MethodCanaryConfigBuilder methodCanaryOutputCallback(MethodCanaryOutputCallback MethodCanaryOutputCallback) {
            this.mMethodCanaryOutputCallback = MethodCanaryOutputCallback;
            return this;
        }

        public MethodCanaryConfig build() {
            MethodCanaryConfig methodCanaryConfig = new MethodCanaryConfig();
            methodCanaryConfig.mMethodCanaryOutputCallback = this.mMethodCanaryOutputCallback;
            methodCanaryConfig.mApp = this.mApp;
            methodCanaryConfig.mMethodEventThreshold = this.mMethodEventThreshold;
            return methodCanaryConfig;
        }
    }
}
