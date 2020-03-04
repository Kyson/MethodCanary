# MethodCanary

[English](https://github.com/Kyson/MethodCanary/blob/master/README.md)

MethodCanary是监控Android方法耗时的工具

这个库用于[AndroidGodEye](https://github.com/Kyson/AndroidGodEye).

[![Build Status](https://travis-ci.org/Kyson/MethodCanary.svg?branch=master)](https://travis-ci.org/Kyson/MethodCanary)

## Quick Start

### Step0 Download

Root build.gradle

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'cn.hikyson.methodcanary:plugin:VERSION'
    }
}
```

Module com.android.application 

```groovy
apply plugin: 'cn.hikyson.methodcanary.plugin'

implementation 'cn.hikyson.methodcanary:lib:VERSION'
```

### Step1 Custom plugin

`cn.hikyson.methodcanary.plugin`有如下开关控制

```groovy
AndroidGodEye {
        enableLifecycleTracer = true // 是否需要页面生命周期方法的插桩，默认true
        enableMethodTracer = true // 是否需要普通的方法插桩，默认true
        instrumentationRuleFilePath = "app/AndroidGodEye-MethodCanary2.js" // 定制插桩逻辑的js文件路径，默认项目根目录AndroidGodEye-MethodCanary.js
        instrumentationRuleIncludeClassNamePrefix = ["cn/hikyson/methodcanary/sample"] // 定制插桩逻辑的类名前缀，默认null
    }
```

> instrumentationRuleFilePath和instrumentationRuleIncludeClassNamePrefix是与关系，满足两者的方法才会插桩

你可以把很多不需要插桩的方法排除掉

1. instrumentationRuleIncludeClassNamePrefix中添加需要插桩的类前缀列表
2. 在项目根目录下创建文件`AndroidGodEye-MethodCanary.js`，文件内容的样例如下:

```
function isInclude(classInfo,methodInfo){
    if(classInfo.name.startsWith('cn/hikyson/methodcanary/samplelib/R$')
            || classInfo.name === 'cn/hikyson/methodcanary/samplelib/BuildConfig'
            || classInfo.name === 'cn/hikyson/methodcanary/samplelib/R'
            || classInfo.name.startsWith('cn/hikyson/methodcanary/sample/R$')
            || classInfo.name === 'cn/hikyson/methodcanary/sample/BuildConfig'
            || classInfo.name === 'cn/hikyson/methodcanary/sample/R'){
            return false
    }
    return true
}
```

#### 注意

1. 不能修改文件中方法的名称和参数: `function isInclude(classInfo,methodInfo)`
2. 这两个方法的返回值必须是bool类型，`isInclude`默认返回true
3. 参数`classInfo`有如下字段: `int access`,`String name`,`String superName`,`String[] interfaces`
4. 参数`methodInfo`有如下字段: `int access`,`String name`,`String desc`
5. 使用javascript语言写`AndroidGodEye-MethodCanary.js`文件

### Step2

```java
// 开始记录方法调用
MethodCanary.get().startMethodTracing("sessionName0")
// 结束记录调用
MethodCanary.get().stopMethodTracing(
                    "sessionName0", MethodCanaryConfig(5)
                ) { sessionTag, startMillis, stopMillis, methodEventMap ->
                    Logger.d("结束！！！")
                }
// 监听页面生命周期的方法耗时
MethodCanary.get().addOnPageLifecycleEventCallback { lifecycleExitMethodEvent, page ->
            Logger.d(page.javaClass.simpleName + lifecycleExitMethodEvent)
        }
MethodCanary.get().removeOnPageLifecycleEventCallback()
```