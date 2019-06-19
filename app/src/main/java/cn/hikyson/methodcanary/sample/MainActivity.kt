package cn.hikyson.methodcanary.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import cn.hikyson.methodcanary.lib.FileUtil
import cn.hikyson.methodcanary.lib.MethodCanaryInject
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.io.File
import java.nio.charset.Charset
import com.orhanobut.logger.PrettyFormatStrategy
import com.orhanobut.logger.FormatStrategy


class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(7)
            .tag("kyson")
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy));
        this.findViewById<Button>(R.id.activity_main_test).setOnClickListener {
            Thread(Runnable {
                for (i in 0..1000) {
                    TestClass.testMethod1()
                    TestClass.testMethod2()
                    TestClass.testMethod3()
                }
            }).start()
        }
        this.findViewById<Button>(R.id.activity_main_monitor).setOnClickListener {
            if (this.isStarted) {
                MethodCanaryInject.stopMonitorAndOutput({
                    val sb = StringBuilder()
                    for (entry in it.entries) {
                        val ti = entry.key
                        sb.append(ti).append("\n")
                        val mes = entry.value
                        for (methodEvent in mes) {
                            sb.append(methodEvent).append("\n")
                        }
                    }
                    Logger.d(sb.toString())
                })
            } else {
                MethodCanaryInject.startMonitor()
            }
            this.isStarted = !this.isStarted
            this.findViewById<Button>(R.id.activity_main_monitor).setText(if (this.isStarted) "stop" else "start")
        }
    }
}
