# Zero dependency Heater Controller
This project is a rewrite of the Central Heating controller (originally written in Clojure) using modern Java.  
The idea is to have it written without any dependencies other than the JDK itself.

## Manual Build
this is a maven project, so it can be built using 
```
$ mvn assembly:assembly -DdescriptorId=jar-with-dependencies
```
## Native Build
This project also contains a script called "build.sh" that will build the jar and natively compile it to a binary file.  
In order to run that, a working installation of the GraalVM is needed with "native-image" support.



