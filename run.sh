#!/bin/bash
rm db/temps.org
DEBUG=true mvn compile exec:java
