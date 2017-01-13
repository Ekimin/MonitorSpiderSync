#!/bin/sh

if [ -z "$JAVA_HOME" ]; then
echo "Please configure the JAVA_HOME!"
exit
fi

export PATH

cd ../

CLASSPATH=.:${JAVA_HOME}/jre/lib/rt.jar:./target/classes
export CLASSPATH

JLIBDIR=./lib
export JLIBDIR

for LL in `ls $JLIBDIR/*.jar`
do
CLASSPATH=$CLASSPATH:$LL
export CLASSPATH
done

JAVA_OPTION="-Dfile.encoding=utf-8 -Xmx1024M -Xms1024M"
RUN_CLASS=com.amarsoft.app.job.LawDataJob

export JAVA_OPTION
export RUN_CLASS

${JAVA_HOME}/bin/java ${JAVA_OPTION} -classpath ${CLASSPATH} ${RUN_CLASS}
