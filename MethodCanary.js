/**
classInfo
    int access
     String name
     String superName
     String[] interfaces

     methodInfo
         int access
         String name
         String desc
**/
function isExclude(classInfo,methodInfo){
    if(classInfo.name.startsWith('android/support/')
    || classInfo.name.startsWith('com/google/gson/')
    || classInfo.name.startsWith('cn/hikyson/methodcanary/samplelib/R$')
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