package cn.hikyson.methodcanary.lib;

import android.content.Context;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class MethodEventPrinter {

    @WorkerThread
    public static String printToFile(Context context, String fileName, Map<ThreadInfo, List<MethodEvent>> methodEvents) {
        File dir = new File(context.getCacheDir(), "MethodCanary");
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                return null;
            }
        }
        File f = new File(dir, fileName);
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<ThreadInfo, List<MethodEvent>> entry : methodEvents.entrySet()) {
            ThreadInfo ti = entry.getKey();
            sb.append(ti).append("\n");
            List<MethodEvent> mes = entry.getValue();
            for (MethodEvent methodEvent : mes) {
                sb.append(methodEvent).append("\n");
            }
        }
        boolean result = FileUtil.writeFileFromBytesByChannel(f, sb.toString().getBytes(Charset.forName("utf-8")), false, true);
        if (!result) {
            return null;
        }
        return f.getAbsolutePath();
    }
}
