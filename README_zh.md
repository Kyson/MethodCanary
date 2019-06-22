# MethodCanary

MethodCanary是记录Android应用代码执行的工具

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

`cn.hikyson.methodcanary.plugin` 默认会给所有方法都注入记录的代码，其中很多其实都是没必要的

你可以把很多不需要插桩的方法排除掉：在项目根目录下创建文件`MethodCanary.js`，文件内容的样例如下:

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

#### 注意

1. 不能修改文件中这两个方法的名称和参数:`function isExclude(classInfo,methodInfo)` and `function isInclude(classInfo,methodInfo)`
2. 这两个方法的返回值必须是bool类型，`isExclude`方法默认返回false，`isInclude`默认返回true
3. 参数`classInfo`有如下字段: `int access`,`String name`,`String superName`,`String[] interfaces`
4. 参数`methodInfo`有如下字段: `int access`,`String name`,`String desc`
5. 使用javascript语言写`MethodCanary.js`文件

### Step2

在Application中初始化MethodCanaryInject

`MethodCanaryInject.init(MethodCanaryConfig methodCanaryConfig)`

构建methodCanaryConfig:

```java
MethodCanaryConfig.MethodCanaryConfigBuilder.aMethodCanaryConfig().app(application).methodEventThreshold(
                1000
            ).methodCanaryOutputCallback(
                { methodEventMap, record ->
                    // methodEventMap:记录方法调用在内存中
                    // record:记录方法调用在文件中
                    // 方法调用一开始记录在内存中，如果记录超过了methodEventThreshold(1000)条，就会写入record文件中
                    // 所以回调之后获取所有的方法调用记录，需要合并methodEventMap和record文件中的内容
                    // 文件中的方法调用的契约：
                    // [THREAD]id=1;name=main;priority=5
                    // PUSH:et=155079652962024;cn=cn/hikyson/methodcanary/sample/MainActivity;ma=25;mn=access$isStarted$p;md=(Lcn/hikyson/methodcanary/sample/MainActivity;)Z
                    // POP:et=155079653206024;cn=cn/hikyson/methodcanary/sample/MainActivity;ma=25;mn=access$isStarted$p;md=(Lcn/hikyson/methodcanary/sample/MainActivity;)Z
                    
                }).build()
```

### Step3

```java
// 开始记录方法调用
MethodCanaryInject.startMonitor()
// 结束方法调用，之后会回调methodCanaryOutputCallback
MethodCanaryInject.stopMonitor()
```