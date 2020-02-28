#!/usr/bin

if [[ `uname` == 'Darwin' ]]; then
    sed -i "" "s#^DISABLE_PLUGIN=.*#DISABLE_PLUGIN=true#g" gradle.properties
fi

if [[ `uname` == 'Linux' ]]; then
    sed -i "s#^DISABLE_PLUGIN=.*#DISABLE_PLUGIN=true#g" gradle.properties
fi



