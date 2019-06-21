package cn.hikyson.methodcanary.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import cn.hikyson.methodcanary.lib.*
import cn.hikyson.methodcanary.samplelib.SampleLibClassA
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(7)
            .tag("MethodCanary")
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy));

        MethodCanaryInject.init(
            MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig().app(application).methodEventThreshold(
                100
            ).methodCanaryOutputCallback(
                { methodEventMap, record ->
                    Logger.d("methodEventMap:\n" + serializeMethodEvent(methodEventMap))
                    Logger.d("record:\n" + readFile2BytesByChannel(record))
                }).build()
        )
        this.findViewById<Button>(R.id.activity_main_test).setOnClickListener {
            Thread(Runnable {
                for (i in 0..1000) {
                    SampleAppClassA.testMethod1()
                    SampleAppClassA.testMethod2()
                    SampleAppClassA.testMethod3()
                }
            }).start()
//            Thread(Runnable {
//                for (i in 0..5000) {
//                    val a = SampleLibClassA("name" + i, i, i % 2 == 0);
//                    a.callMe()
//                    a.growup()
//                }
//            }).start()
        }
        this.findViewById<Button>(R.id.activity_main_monitor).setOnClickListener {
            if (this.isStarted) {
                MethodCanaryInject.stopMonitorAndOutput()
            } else {
                MethodCanaryInject.startMonitor()
            }
            this.isStarted = !this.isStarted
            this.findViewById<Button>(R.id.activity_main_monitor).setText(if (this.isStarted) "stop" else "start")
        }
    }


    internal fun serializeMethodEvent(methodEventMap: Map<ThreadInfo, List<MethodEvent>>): String {
        val sb = StringBuilder()
        for ((key, mes) in methodEventMap) {
            sb.append(threadInfo2String(key)).append("\n")
            for (methodEvent in mes) {
                sb.append(methodEvent).append("\n")
            }
        }
        return sb.toString()
    }

    private fun threadInfo2String(threadInfo: ThreadInfo?): String {
        return if (threadInfo == null) {
            "[THREAD] NULL"
        } else "[THREAD]" + "id=" + threadInfo.id + ";name=" + threadInfo.name + ";priority=" + threadInfo.priority
    }

    internal fun readFile2BytesByChannel(file: File): ByteArray? {
        if (!isFileExists(file)) {
            return null
        }
        var fc: FileChannel? = null
        try {
            fc = RandomAccessFile(file, "r").channel
            val byteBuffer = ByteBuffer.allocate(fc!!.size().toInt())
            while (true) {
                if (fc.read(byteBuffer) <= 0) {
                    break
                }
            }
            return byteBuffer.array()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                fc?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    private fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }
}
