#!/bin/bash
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
native-image \
	-H:SerializationConfigurationFiles=./config/serialization-config.json \
	-H:ReflectionConfigurationFiles=./config/reflect-config.json \
	-H:JNIConfigurationFiles=./config/jni-config.json \
  --static \
  --enable-http \
  --enable-https \
	-jar target/heater-1.1-jar-with-dependencies.jar
