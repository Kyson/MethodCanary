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
                MethodCanaryInject.startMonitor(
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
                Logger.d("start monitor")
            }
            this.isStarted = !this.isStarted
            this.findViewById<Button>(R.id.activity_main_monitor)
                .setText(if (this.isStarted) "stop" else "start")
        }
        this.findViewById<Button>(R.id.activity_main_random).setOnClickListener {
            randomApi()
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
            for (i in 0..2000) {
                Thread.sleep(15)
                try {
                    MethodCanaryInject.startMonitor(
                        MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig()
                            .lowCostThreshold(1000000 * 150)
                            .methodCanaryCallback(object : MethodCanaryCallback {

                                override fun onStopped(
                                    startTimeNanos: Long,
                                    stopTimeNanos: Long
                                ) {
                                }

                                override fun outputToMemory(
                                    startTimeNanos: Long,
                                    stopTimeNanos: Long,
                                    methodEventMap: Map<ThreadInfo, List<MethodEvent>>
                                ) {
                                    Logger.d(
                                        "startTimeNanos:%s, stopTimeNanos:%s, outputToMemory:\n%s",
                                        startTimeNanos,
                                        stopTimeNanos,
                                        methodEventMap
                                    )
                                }
                            })
                            .build()
                    )
                } catch (e: Throwable) {
                }
            }
        }).start()
        Thread(Runnable {
            for (i in 0..2000) {
                Thread.sleep(20)
                MethodCanaryInject.stopMonitor()
            }
        }).start()
    }

}
