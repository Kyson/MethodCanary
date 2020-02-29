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

By default, `cn.hikyson.methodcanary.plugin` will inject code to all methods, include a lot of unnecessary methods.

You can change this behavior by put js file `AndroidGodEye-MethodCanary.js` in project root, content sample:

```
function isInclude(classInfo,methodInfo){
    if(!classInfo.name.startsWith('cn/hikyson/methodcanary')){
        return false;
    }
    if(classInfo.name.startsWith('android/support/')
            || classInfo.name.startsWith('com/google/gson/')
            || classInfo.name.startsWith('cn/hikyson/methodcanary/samplelib/R$')
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
MethodCanary.get().start("sessionName0")
// stop recording
MethodCanary.get().stop(
                    "sessionName0", MethodCanaryConfig(5*1000000)
                ) { sessionTag, startNanoTime, stopNanoTime, methodEventMap ->
                    Logger.d("finish！！！")
                }
```