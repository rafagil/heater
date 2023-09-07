# Zero dependency Heater Controller
This project is a rewrite of the Central Heating controller (originally written in Clojure) using modern Java.  
The idea is to have it written without any dependencies other than the JDK itself (That explains using csv and xml files for configuration).

## What is this for?
The objective of this software is to control central heating and hot water assuming those can be controlled using an IoT device.  
It provides simple HTTP endpoints that can be used to update the temperature for a specific zone, set a desired temperature etc.  
It also provides a mechanism to Schedule temperature changes throughout the day, "Out of home" schedule changes, Hot water timers and even
estimate prepaid gas balance.  
There is no UI at the moment, but it's meant to be easily integrated with mobile devices using Apple Shortcuts (or anything similar on Android) and IFTTT (for notifications).

## How to build it?
### Manual Build
this is a maven project, so it can be built using 
```
$ mvn compile assembly:single
```
### Native Build
This project also contains a script called "build.sh" that will build the jar and natively compile it to a binary file.  
In order to run that, a working installation of the GraalVM is needed with "native-image" support.

## How to use it?
First, configure your schedule and hw-timers csv files using the examples in the "config" directory.  
Then Either set up the http-adapter.xml if your IoT devices use HTTP for communication or implement a new Adpater (using the Adapter interface) and use it instead.  
Then, just build it and run the server with a port as its parameter:
```
$ ./heater 3000
```
And check the current status on:
```
http://localhost:3000/status


```


