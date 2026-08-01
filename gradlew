#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
#
# Gradle start up script for POSIX compatible shells
#
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Default JVM options
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use Java bundled with Android Studio1 if JAVA_HOME is not set
if [ -z "$JAVA_HOME" ] ; then
    JAVA_HOME="/c/Program Files/Android/Android Studio1/jbr"
fi

JAVACMD="$JAVA_HOME/bin/java"

# Determine the location of this script
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then PRG="$link"; else PRG=`dirname "$PRG"`"/$link"; fi
done
PRGDIR=`dirname "$PRG"`

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    -classpath "$PRGDIR/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain "$@"
