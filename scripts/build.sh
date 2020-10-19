#!/bin/bash
if ! command -v mvn -version &> /dev/null
then
    echo "Maven not found,please install and try again"
    exit
fi
if [ -z "$JAVA_HOME" ]
then
    echo "Build can't proceed as JAVA_HOME is not set, please trying setting it"
    exit
fi
echo "Starting maven build now"
mvn -f ../pom.xml clean install
