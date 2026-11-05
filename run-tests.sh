#!/bin/sh
set -eu
cd "$(dirname "$0")"
if [ "$(uname -s)" = "Darwin" ]; then
    JAVA_HOME=$(/usr/libexec/java_home -v 11 -a arm64 2>/dev/null || /usr/libexec/java_home -v 11)
    export JAVA_HOME
fi
MAVEN_OPTS=${MAVEN_OPTS:--Xmx256m}
export MAVEN_OPTS
exec mvn -B -ntp clean test allure:report "$@"
