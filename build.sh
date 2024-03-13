#!/bin/bash
mvn clean compile assembly:single &&
native-image \
  -H:SerializationConfigurationFiles=./config/serialization-config.json \
  -H:JNIConfigurationFiles=./config/jni-config.json \
  --static \
  --gc=G1 \
  --enable-http \
  --enable-https \
  -march=native \
  -jar target/heater-1.2-jar-with-dependencies.jar
