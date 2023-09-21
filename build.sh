#!/bin/bash
mvn clean compile assembly:single &&
native-image \
  -H:SerializationConfigurationFiles=./config/serialization-config.json \
  -H:ReflectionConfigurationFiles=./config/reflect-config.json \
  -H:JNIConfigurationFiles=./config/jni-config.json \
  --static \
  --gc=G1 \
  --enable-http \
  --enable-https \
  -march=native \
  -jar target/heater-1.1-jar-with-dependencies.jar
