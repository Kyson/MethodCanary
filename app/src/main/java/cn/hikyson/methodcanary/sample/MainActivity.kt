package cn.hikyson.methodcanary.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import cn.hikyson.methodcanary.lib.*
import cn.hikyson.methodcanary.samplelib.SampleLibClassA


class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.findViewById<Button>(R.id.activity_main_test).setOnClickListener {
            Thread(Runnable {
                for (i in 0..1000) {
                    SampleAppClassA.testMethod1()
                    SampleAppClassA.testMethod2()
                    SampleAppClassA.testMethod3()
                }
            }).start()
            Thread(Runnable {
                for (i in 0..5000) {
                    val a = SampleLibClassA("name" + i, i, i % 2 == 0);
                    a.callMe()
                    a.growup()
                }
            }).start()
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
}
