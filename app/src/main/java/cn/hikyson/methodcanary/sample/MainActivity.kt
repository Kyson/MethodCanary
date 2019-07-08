package cn.hikyson.methodcanary.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import cn.hikyson.methodcanary.lib.*
import cn.hikyson.methodcanary.samplelib.SampleLibClassA
import com.orhanobut.logger.Logger


class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.findViewById<Button>(R.id.activity_main_test_once).setOnClickListener {
            SampleAppClassA.testMethod1(5)
        }
        this.findViewById<Button>(R.id.activity_main_test).setOnClickListener {
            Thread(Runnable {
                Logger.d("开始执行SampleAppClassA中的方法")
                for (i in 0..8000) {
                    SampleAppClassA.testMethod1(5)
                    SampleAppClassA.testMethod2()
                    SampleAppClassA.testMethod3()
                }
                Logger.d("结束执行SampleAppClassA中的方法")
            }).start()
            Thread(Runnable {
                Logger.d("开始执行SampleAppClassA中的方法")
                for (i in 0..8000) {
                    SampleAppClassA.testMethod1(5)
                    SampleAppClassA.testMethod2()
                    SampleAppClassA.testMethod3()
                }
                Logger.d("结束执行SampleAppClassA中的方法")
            }).start()
            Thread(Runnable {
                Logger.d("开始执行SampleAppClassA中的方法")
                for (i in 0..8000) {
                    SampleAppClassA.testMethod1(5)
                    SampleAppClassA.testMethod2()
                    SampleAppClassA.testMethod3()
                }
                Logger.d("结束执行SampleAppClassA中的方法")
            }).start()
            Thread(Runnable {
                Logger.d("开始执行SampleLibClassA中的方法")
                for (i in 0..8000) {
                    val a = SampleLibClassA("name" + i, i, i % 2 == 0);
                    a.callMe()
                    a.growup(5);
                }
                Logger.d("结束执行SampleLibClassA中的方法")
            }).start()
            Thread(Runnable {
                Logger.d("开始执行SampleLibClassA中的方法")
                for (i in 0..8000) {
                    val a = SampleLibClassA("name" + i, i, i % 2 == 0);
                    a.callMe()
                    a.growup(5);
                }
                Logger.d("结束执行SampleLibClassA中的方法")
            }).start()
            Thread(Runnable {
                Logger.d("开始执行SampleLibClassA中的方法")
                for (i in 0..8000) {
                    val a = SampleLibClassA("name" + i, i, i % 2 == 0);
                    a.callMe()
                    a.growup(5);
                }
                Logger.d("结束执行SampleLibClassA中的方法")
            }).start()
        }
        this.findViewById<Button>(R.id.activity_main_monitor).setOnClickListener {
            if (this.isStarted) {
                MethodCanaryInject.stopMonitor()
                Logger.d("stop monitor")
            } else {
                MethodCanaryInject.startMonitor()
                Logger.d("start monitor")
            }
            this.isStarted = !this.isStarted
            this.findViewById<Button>(R.id.activity_main_monitor).setText(if (this.isStarted) "stop" else "start")
        }
    }
}
