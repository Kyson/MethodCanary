package cn.hikyson.methodcanary.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import cn.hikyson.methodcanary.lib.*
import cn.hikyson.methodcanary.samplelib.SampleLibClassA
import com.orhanobut.logger.Logger


open class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.findViewById<Button>(R.id.activity_main_to_main2).setOnClickListener {
            val i = Intent(this@MainActivity, Main2Activity::class.java)
            startActivity(i)
        }
        this.findViewById<Button>(R.id.activity_main_test_once).setOnClickListener {
            SampleAppClassA.testMethod1(5)
        }
        this.findViewById<Button>(R.id.activity_main_test).setOnClickListener {
            Thread(Runnable {
                Logger.d("开始执行SampleAppClassA中的方法")
                for (i in 0..10000) {
                    SampleAppClassA.testMethod1(4)
                    SampleAppClassA.testMethod2()
                    SampleAppClassA.testMethod3()
                }
                Logger.d("结束执行SampleAppClassA中的方法")
            }).start()
            Thread(Runnable {
                Logger.d("开始执行SampleLibClassA中的方法")
                for (i in 0..10000) {
                    val a = SampleLibClassA("name" + i, i, i % 2 == 0);
                    a.callMe()
                    a.growup(5);
                }
                Logger.d("结束执行SampleLibClassA中的方法")
            }).start()
        }
        this.findViewById<Button>(R.id.activity_main_monitor).setOnClickListener {
            if (this.isStarted) {
                MethodCanary.get().stopMethodTracing(
                    "1", MethodCanaryConfig(1)
                ) { sessionTag, startNanoTime, stopNanoTime, methodEventMap ->
                    //                    Logger.d(methodEventMap2String(methodEventMap))
                    Logger.d("结束！！！")
                }
            } else {
                MethodCanary.get().startMethodTracing("1")
            }
            this.isStarted = !this.isStarted
            this.findViewById<Button>(R.id.activity_main_monitor)
                .setText(if (this.isStarted) "stop" else "start")
        }
        this.findViewById<Button>(R.id.activity_main_random).setOnClickListener {
            randomApi()
        }

        this.findViewById<Button>(R.id.activity_main_to_main3_fake).setOnClickListener {
            var main3FakeActivity = Main3FakeActivity()
            main3FakeActivity.onCreate(null)
            main3FakeActivity.onStop()
            Thread(Runnable {
                Thread.sleep(2000)
                Child1Fragment().onPause()
            }).start()
        }
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

    private fun randomApi() {
        Thread(Runnable {
            for (i in 0..1000) {
                Thread.sleep(8)
                MethodCanary.get().startMethodTracing(i.toString())
            }
        }).start()
        Thread(Runnable {
            for (i in 0..1000) {
                Thread.sleep(12)
                MethodCanary.get().stopMethodTracing(
                    i.toString(),
                    MethodCanaryConfig(1)
                ) { sessionTag, startNanoTime, stopNanoTime, methodEventMap ->

                }
            }
        }).start()
    }

    override fun onStop() {
        super.onStop()
    }

}
