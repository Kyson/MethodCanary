#!/usr/bin

sh disable_plugin.sh

./gradlew :plugin:clean :plugin:bintrayUpload -PbintrayUser=kyson -PbintrayKey=$BINTRAY_KEY -PdryRun=false

./gradlew :lib:clean :lib:bintrayUpload -PbintrayUser=kyson -PbintrayKey=$BINTRAY_KEY -PdryRun=false