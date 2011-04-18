#! /bin/sh
if [ $# -eq 0 ] || [ $# -ne 3 ]; then
   cat << EOF >&2
   Useage: $0 dumpDir pid dumpClasses
   
   dumpDir 		指定dump class后的目录
   pid			指定jvm pid
   dumpClasses		指定dump class的正则匹配，中间用,分隔
EOF
 exit 1
fi

BASE=`basename "$0"`
ATTACHER="com.agapple.tracer4java.dump.instrument.Attacher"

DUMPDIR=$1
PID=$2
DUMPCLASSES=$3

${JAVA_HOME}/bin/java -classpath "$BASE/lib:." $ATTACHER $PID $BASE/lib/tracer4java-1.0.0-SNAPSHOT.jar dumpDir=$DUMPDIR,dumpClasses=$DUMPCLASSES