package cn.hikyson.methodcanary.lib;


import android.util.Log;

class MethodCanaryLogger {
    private static final String TAG = "AndroidGodEye";
    private static boolean sEnableLog = false;

    static void enableLog(boolean b) {
        sEnableLog = b;
    }

    static void log(String msg) {
        if (sEnableLog) {
            Log.d(TAG, msg);
        }
    }
}
