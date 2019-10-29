package cn.hikyson.methodcanary.sample

import android.app.Application
import cn.hikyson.methodcanary.lib.*
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

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

//        Thread(Runnable {
//            for (i in 0..100) {
//                Thread.sleep(502)
//                MethodCanaryInject.install(
//                    MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
//                        .lowCostThreshold(1000000 * 150)
//                        .methodCanaryCallback(object : MethodCanaryCallback {
//
//                            override fun onStopped(
//                                startTimeNanos: Long,
//                                stopTimeNanos: Long
//                            ) {
//
//                            }
//
//                            override fun outputToMemory(
//                                startTimeNanos: Long,
//                                stopTimeNanos: Long,
//                                methodEventMap: Map<ThreadInfo, List<MethodEvent>>
//                            ) {
//                                Logger.d(
//                                    "startTimeNanos:%s, stopTimeNanos:%s, outputToMemory:\n%s",
//                                    startTimeNanos,
//                                    stopTimeNanos,
//                                    methodEventMap
//                                )
//                                MethodCanaryInject.uninstall()
//                            }
//                        })
//                        .build()
//                )
//            }
//        }).start()
//
//        Thread(Runnable {
//            for (i in 0..100) {
//                Thread.sleep(401)
//                MethodCanaryInject.uninstall()
//            }
//        }).start()
//
//        Thread(Runnable {
//            for (i in 0..200) {
//                Thread.sleep(231)
//                try {
//                    MethodCanaryInject.startMonitor()
//                } catch (e: Throwable) {
//                }
//            }
//        }).start()
//        Thread(Runnable {
//            for (i in 0..200) {
//                Thread.sleep(171)
//                MethodCanaryInject.stopMonitor()
//            }
//        }).start()


        Thread(Runnable {
            MethodCanaryInject.install(
                MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
                    .lowCostThreshold(1000000 * 6)
                    .methodCanaryCallback(object : MethodCanaryCallback {

                        override fun onStopped(
                            startTimeNanos: Long,
                            stopTimeNanos: Long
                        ) {
                            Logger.d("正在停止...")
                        }

                        override fun outputToMemory(
                            startTimeNanos: Long,
                            stopTimeNanos: Long,
                            methodEventMap: Map<ThreadInfo, List<MethodEvent>>
                        ) {
                            Logger.d(
                                "收到内存中的消息，共%s个线程：startTimeNanos:%s, stopTimeNanos:%s, outputToMemory:\n%s",
                                methodEventMap.size,
                                startTimeNanos,
                                stopTimeNanos,
                                methodEventMap2String(methodEventMap)
                            )
                        }
                    })
                    .build()
            )
            Logger.d("已经安装")
        }).start()
    }

    private fun methodEventMap2String(methodEventMap: Map<ThreadInfo, List<MethodEvent>>?): String {
        val sb = StringBuilder()
        for (entry in methodEventMap?.entries!!) {
            sb.append(entry.key).append("\n")
            for (method in entry.value) {
                sb.append(method).append("\n")
            }
            sb.append("\n")
        }
        return String(sb)
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