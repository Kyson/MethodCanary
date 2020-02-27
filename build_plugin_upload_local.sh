#!/usr/bin

sh disable_plugin.sh

rm -rf repos

./gradlew :plugin:clean :plugin:uploadArchives

sh enable_plugin.sh.sh
