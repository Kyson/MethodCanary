# MethodCanary

[中文](https://github.com/Kyson/MethodCanary/blob/master/README_zh.md)

MethodCanary is tool to record method invocations

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

By default, `cn.hikyson.methodcanary.plugin` will inject code to all methods, include a lot of unnecessary

you can change this behavior by put js file `MethodCanary.js` in project root, content sample:

```
function isExclude(classInfo,methodInfo){
    if(
    classInfo.name.startsWith('cn/hikyson/methodcanary/samplelib/R$')
    || classInfo.name === 'cn/hikyson/methodcanary/samplelib/BuildConfig'
    || classInfo.name === 'cn/hikyson/methodcanary/samplelib/R'
    || classInfo.name.startsWith('cn/hikyson/methodcanary/sample/R$')
    || classInfo.name === 'cn/hikyson/methodcanary/sample/BuildConfig'
    || classInfo.name === 'cn/hikyson/methodcanary/sample/R'){
        return true
    }
    return false
}

function isInclude(classInfo,methodInfo){
    return classInfo.name.startsWith('cn/hikyson/methodcanary')
}
```

#### Note

1. can not change function name and desc:`function isExclude(classInfo,methodInfo)` and `function isInclude(classInfo,methodInfo)`
2. these two functions must return boolean, default false for `isExclude`, true for `isInclude`
3. param `classInfo` has fields: `int access`,`String name`,`String superName`,`String[] interfaces`
4. param `methodInfo` has fields: `int access`,`String name`,`String desc`
5. write `MethodCanary.js` by javascript language

### Step2

Init MethodCanaryInject in application

`MethodCanaryInject.init(MethodCanaryConfig methodCanaryConfig)`

Build methodCanaryConfig:

```java
MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig().app(application).methodEventThreshold(
                1000
            ).methodCanaryOutputCallback(
                { methodEventMap, record ->
                    // methodEventMap:store method event in memory
                    // record:store method event in file
                    // if method event over methodEventThreshold(1000),then methodEventMap->record
                    // if you want to get all method events，you should merge methodEventMap and record
                    // record file's contract：
                    // [THREAD]id=1;name=main;priority=5
                    // PUSH:et=155079652962024;cn=cn/hikyson/methodcanary/sample/MainActivity;ma=25;mn=access$isStarted$p;md=(Lcn/hikyson/methodcanary/sample/MainActivity;)Z
                    // POP:et=155079653206024;cn=cn/hikyson/methodcanary/sample/MainActivity;ma=25;mn=access$isStarted$p;md=(Lcn/hikyson/methodcanary/sample/MainActivity;)Z                
                }).build()
```

### Step3

```java
MethodCanaryInject.startMonitor()
MethodCanaryInject.stopMonitor()
```