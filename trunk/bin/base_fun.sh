#!/bin/sh
PRG="$0"
progname=`basename "$0"`
BASE=`dirname "$PRG"`/..
BASE=`cd "$BASE" && pwd`
LIB_PATH="$BASE/lib"
LOCALCLASSPATH=`echo $LIB_PATH/*.jar | tr ' ' ':'`
export CLASSPATH=$LOCALCLASSPATH:${JAVA_HOME}/lib/sa-jdi.jar:${JAVA_HOME}/lib/tools.jar