package cn.hikyson.methodcanary.lib;

import android.content.Context;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

public class Util {
    private static final String RECORD_DIR_NAME = "method_canary";
    private static final String RECORD_FILE_NAME = "method_events.tmp";

    static File ensureRecordFile(Context context) throws FileNotFoundException {
        File dir = new File(context.getCacheDir(), RECORD_DIR_NAME);
        if (dir.exists() && !dir.isDirectory()) {
            if (!dir.delete()) {
                throw new FileNotFoundException("method_canary file is not dir and can not be deleted.");
            }
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new FileNotFoundException("method_canary is not exist and can not be mk.");
            }
        }
        return new File(dir, RECORD_FILE_NAME);
    }

    static File getRecordFile(Context context) {
        File dir = new File(context.getCacheDir(), RECORD_DIR_NAME);
        return new File(dir, RECORD_FILE_NAME);
    }

    static void deleteRecordFile(Context context) {
        File f = getRecordFile(context);
        boolean result = f.delete();
    }

    static String serializeMethodEvent(Map<ThreadInfo, List<MethodEvent>> methodEventMap) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<ThreadInfo, List<MethodEvent>> entry : methodEventMap.entrySet()) {
            sb.append(threadInfo2String(entry.getKey())).append("\n");
            List<MethodEvent> mes = entry.getValue();
            for (MethodEvent methodEvent : mes) {
                sb.append(methodEvent).append("\n");
            }
        }
        return String.valueOf(sb);
    }

    private static String threadInfo2String(ThreadInfo threadInfo) {
        if (threadInfo == null) {
            return "[THREAD] NULL";
        }
        return "[THREAD]" + "id=" + threadInfo.id + ";name=" + threadInfo.name + ";priority=" + threadInfo.priority;
    }

    static boolean writeFileFromBytesByChannel(final File file,
                                               final byte[] bytes,
                                               final boolean append,
                                               final boolean isForce) {
        if (bytes == null) {
            return false;
        }
        FileChannel fc = null;
        try {
            fc = new FileOutputStream(file, append).getChannel();
            fc.position(fc.size());
            fc.write(ByteBuffer.wrap(bytes));
            if (isForce) {
                fc.force(true);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
