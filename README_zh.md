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

releaseImplementation 'cn.hikyson.methodcanary:lib:VERSION'
debugImplementation 'cn.hikyson.methodcanary:libnoop:VERSION'
```

### Step1 Custom plugin

`cn.hikyson.methodcanary.plugin` 默认会给所有方法都注入记录的代码，其中很多其实都是没必要的

你可以把很多不需要插桩的方法排除掉：在项目根目录下创建文件`AndroidGodEye-MethodCanary.js`，文件内容的样例如下:

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

#### 注意

1. 不能修改文件中方法的名称和参数: `function isInclude(classInfo,methodInfo)`
2. 这两个方法的返回值必须是bool类型，`isInclude`默认返回true
3. 参数`classInfo`有如下字段: `int access`,`String name`,`String superName`,`String[] interfaces`
4. 参数`methodInfo`有如下字段: `int access`,`String name`,`String desc`
5. 使用javascript语言写`AndroidGodEye-MethodCanary.js`文件

### Step2

```java
// 开始记录方法调用
MethodCanary.get().start("sessionName0")
// 结束记录调用
MethodCanary.get().stop(
                    "sessionName0", MethodCanaryConfig(5*1000000)
                ) { sessionTag, startNanoTime, stopNanoTime, methodEventMap ->
                    Logger.d("结束！！！")
                }
```