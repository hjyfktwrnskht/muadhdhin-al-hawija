#!/usr/bin/env sh
DEFAULT_JVM_OPTS="-Xmx64m"
APP_NAME="Gradle"
APP_BASE_NAME="gradle"
APP_HOME="$(cd "$(dirname "$0")" ; pwd -P)"
APP_PID="$APP_HOME/.${APP_BASE_NAME}.pid"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
exec java $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
