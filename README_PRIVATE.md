
# Build Plugin

修改文件./plugin/build.gradle
pom.version = 0.101

执行uploadArchives打包到本地repo

打包完成后，修改根目录的./build.gradle
classpath 'cn.hikyson.methodcanary:plugin:0.101'

如果版本号之类的有问题，可以注释掉文件./app/build.gradle
apply plugin: 'cn.hikyson.methodcanary.plugin'

# Plugin Deploy

./gradlew :plugin bintrayUpload

# Library Deploy

1. 修改根目录gradle.properties
2. git tag
3. git push

[https://travis-ci.org/Kyson/MethodCanary](https://travis-ci.org/Kyson/MethodCanary)