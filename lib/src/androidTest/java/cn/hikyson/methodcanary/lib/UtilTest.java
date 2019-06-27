package cn.hikyson.methodcanary.lib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import okio.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void merge() throws IOException {
        Context context = InstrumentationRegistry.getTargetContext();
        File file = new File(context.getCacheDir(), Util.RECORD_FILE_NAME);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        String content = "[THREAD]id=2;name=main;priority=5\n" +
                "PUSH:et=3284436162929793;cn=cn/hikyson/methodcanary/sample/MainActivity;ma=1;mn=<init>;md=()V\n" +
                "POP:et=3284436163062866;cn=cn/hikyson/methodcanary/sample/MainActivity;ma=1;mn=<init>;md=()V\n" +
                "PUSH:et=3284436172374273;cn=cn/hikyson/methodcanary/sample/MainActivity;ma=4;mn=onCreate;md=(Landroid/os/Bundle;)V\n" +
                "PUSH:et=3284436240967658;cn=cn/hikyson/methodcanary/sample/MainActivity$onCreate$1;ma=8;mn=<clinit>;md=()V\n" +
                "[THREAD]id=147348;name=Thread-17;priority=5\n" +
                "PUSH:et=3284440030357813;cn=cn/hikyson/methodcanary/sample/SampleAppClassA$1;ma=1;mn=run;md=()V\n" +
                "[THREAD]id=147339;name=Thread-8;priority=5\n" +
                "PUSH:et=3284436994094897;cn=cn/hikyson/methodcanary/sample/MainActivity$onCreate$1$2;ma=17;mn=run;md=()V\n" +
                "PUSH:et=3284436994219533;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=<init>;md=(Ljava/lang/String;IZ)V\n" +
                "POP:et=3284436994257397;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=<init>;md=(Ljava/lang/String;IZ)V\n" +
                "PUSH:et=3284436994285783;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=callMe;md=()V\n" +
                "POP:et=3284436994393231;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=callMe;md=()V\n" +
                "PUSH:et=3284436994444376;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=growup;md=()V\n" +
                "POP:et=3284437994845418;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=growup;md=()V\n" +
                "PUSH:et=3284437994971876;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=<init>;md=(Ljava/lang/String;IZ)V\n" +
                "POP:et=3284437995006251;cn=cn/hikyson/methodcanary/samplelib/SampleLibClassA;ma=1;mn=<init>;md=(Ljava/lang/String;IZ)V\n";
        try (Sink fileSink = Okio.sink(file);
             BufferedSink bufferedSink = Okio.buffer(fileSink)) {
            bufferedSink.writeUtf8(content);
            bufferedSink.flush();
        }


        Map<ThreadInfo, List<MethodEvent>> methodEventMap = new HashMap<>();
        List<MethodEvent> methodEvents = new ArrayList<>();
        methodEvents.add(new MethodExitEvent("cn/hikyson/methodcanary/sample/MainActivity$onCreate$1", 8, "<clinit>", "()V", 3284436240967666L));
        methodEvents.add(new MethodExitEvent("cn/hikyson/methodcanary/sample/MainActivity", 4, "onCreate", "(Landroid/os/Bundle;)V", 3284436240967690L));
        methodEventMap.put(new ThreadInfo(2, "main", 5), methodEvents);


        List<MethodEvent> methodEvents2 = new ArrayList<>();
        methodEvents2.add(new MethodExitEvent("cn/hikyson/methodcanary/sample/MainActivity$onCreate$1", 8, "<clinit>", "()V", 3284436240967666L));
        methodEvents2.add(new MethodExitEvent("cn/hikyson/methodcanary/sample/MainActivity", 4, "onCreate", "(Landroid/os/Bundle;)V", 3284436240967690L));
        methodEventMap.put(new ThreadInfo(3, "main222", 5), methodEvents2);

        List<MethodEvent> methodEvents3 = new ArrayList<>();
        methodEvents3.add(new MethodEnterEvent("cn/hikyson/methodcanary/sample/xxxx", 4, "methodNamexxx", "(Landroid/os/Bundle;)V", 3284437995006300L));
        methodEventMap.put(new ThreadInfo(147339, "Thread-8", 5), methodEvents3);

        boolean result = Util.merge(file, methodEventMap);
        assertTrue(result);
        printFile(file);
    }

    public void printFile(File file) throws IOException {
        try (Source fileSource = Okio.source(file);
             BufferedSource bufferedSource = Okio.buffer(fileSource)) {
            System.out.println(bufferedSource.readUtf8());
        }
    }

}