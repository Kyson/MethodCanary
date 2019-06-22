package cn.hikyson.methodcanary.sample

import android.app.Application
import cn.hikyson.methodcanary.lib.MethodCanaryConfig
import cn.hikyson.methodcanary.lib.MethodCanaryInject
import cn.hikyson.methodcanary.lib.MethodEvent
import cn.hikyson.methodcanary.lib.ThreadInfo
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(7)
            .tag("MethodCanary")
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy));
        MethodCanaryInject.init(
            MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig().app(this).methodEventThreshold(
                30
            ).methodCanaryOutputCallback(
                { methodEventMap, record ->
                    Logger.d("methodEventMap:\n" + serializeMethodEvent(methodEventMap))
                    Logger.d("record:\n" + readFile2BytesByChannel(record)?.let {
                        String(
                            it,
                            Charset.forName("utf-8")
                        )
                    })
                }).build()
        )
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