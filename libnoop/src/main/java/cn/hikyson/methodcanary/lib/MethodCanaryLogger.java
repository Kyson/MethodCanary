package cn.hikyson.methodcanary.lib;


import android.util.Log;

class MethodCanaryLogger {
    private static final String TAG = "MethodCanary";

    static void log(String msg) {
        Log.d(TAG, msg);
    }
}
