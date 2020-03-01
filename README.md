# MethodCanary

[中文](https://github.com/Kyson/MethodCanary/blob/master/README_zh.md)

MethodCanary is tool to metric method cost.

Written for [AndroidGodEye](https://github.com/Kyson/AndroidGodEye).

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

releaseImplementation 'cn.hikyson.methodcanary:lib:VERSION'
debugImplementation 'cn.hikyson.methodcanary:libnoop:VERSION'
```

### Step1 Custom plugin

`cn.hikyson.methodcanary.plugin` has some configurations.

```groovy
AndroidGodEye {
        enableLifecycleTracer = true // Need lifecycle methods instrumentation, default true
        enableMethodTracer = true // Need common methods instrumentation, default true
        instrumentationRuleFilePath = "app/AndroidGodEye-MethodCanary2.js" // Default AndroidGodEye-MethodCanary.js
        instrumentationRuleIncludeClassNamePrefix = ["cn/hikyson/methodcanary/sample"] // Default null
    }
```

> Methods Meet this condition `instrumentationRuleFilePath && instrumentationRuleIncludeClassNamePrefix` will be instrumented

You can change instrumentation rule

1. Add class prefix list to `instrumentationRuleIncludeClassNamePrefix`
2. Put js file `AndroidGodEye-MethodCanary.js` in project root, content sample:

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

#### Note

1. Can not change function name and desc: `function isInclude(classInfo,methodInfo)`
2. Functions must return boolean type, default True for `isInclude`
3. Param `classInfo` has fields: `int access`,`String name`,`String superName`,`String[] interfaces`
4. Param `methodInfo` has fields: `int access`,`String name`,`String desc`
5. Write `AndroidGodEye-MethodCanary.js` by javascript language

### Step2

```java
// start recording
MethodCanary.get().startMethodTracing("sessionName0")
// stop recording
MethodCanary.get().stopMethodTracing(
                    "sessionName0", MethodCanaryConfig(5*1000000)
                ) { sessionTag, startNanoTime, stopNanoTime, methodEventMap ->
                    Logger.d("finish！！！")
                }
// Observe page lifecycle method cost
MethodCanary.get().setOnPageLifecycleEventCallback { lifecycleExitMethodEvent, page ->
            Logger.d(page.javaClass.simpleName + lifecycleExitMethodEvent)
        }
```