package cn.hikyson.methodcanary.sample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.hikyson.methodcanary.lib.MethodCanary
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
            .tag("AndroidGodEye")
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy));
        MethodCanary.get().setOnPageLifecycleEventCallback { lifecycleMethodEvent, page ->
            if (lifecycleMethodEvent.isEnter) {
                Logger.d(
                    "[%s] %s start",
                    page.javaClass.name + page.hashCode() + "|" + lifecycleMethodEvent.className,
                    lifecycleMethodEvent.methodName
                )
            } else {
                Logger.d(
                    "[%s] %s cost %s ms",
                    page.javaClass.name + page.hashCode() + "|" + lifecycleMethodEvent.className,
                    lifecycleMethodEvent.methodName,
                    (lifecycleMethodEvent.eventNanoTime - lifecycleMethodEvent.pairMethodEvent.eventNanoTime) / 1000000
                )
            }
        }
        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                // activity.onCreated start -> activity.super.onCreated -> onActivityCreated -> activity.onCreated end
            }
        })
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