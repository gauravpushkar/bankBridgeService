#!/bin/bash
echo "Starting the Jetty Server"
java -Xms1024m -Xmx2048m -XX:+UseG1GC  -XX:+HeapDumpOnOutOfMemoryError -jar ../target/codingchallenge-1.0-launcher.jar
