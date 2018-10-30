#!/bin/bash
# Assign backing service addresses from the outer environment

export DB_USERNAME=${DATABASE_USERNAME:-intyg}
export DB_PASSWORD=${DATABASE_PASSWORD:-intyg}

export ACTIVEMQ_BROKER_USERNAME=${ACTIVEMQ_BROKER_USERNAME:-admin}
export ACTIVEMQ_BROKER_PASSWORD=${ACTIVEMQ_BROKER_PASSWORD:-admin}

export REDIS_PASSWORD=${REDIS_PASSWORD:-redis}

# dev profile is default for pipeline
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-test,rhs-security-test,rhs-it-stub,wc-hsa-stub,wc-pu-stub,testability-api,caching-enabled,rhs-srs-stub,rhs-sparrtjanst-stub,rhs-samtyckestjanst-stub}

export CATALINA_OPTS_APPEND="\
-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
-Dconfig.folder=/opt/$APP_NAME/config \
-Dconfig.file=/opt/$APP_NAME/config/rehabstod.properties \
-Dlogback.file=classpath:logback-ocp.xml \
-Dcertificate.folder=/opt/$APP_NAME/certifikat \
-Dcredentials.file=/opt/$APP_NAME/env/secret-env.properties \
-Dresources.folder=/tmp/resources \
-Dfile.encoding=UTF-8 \
-DbaseUrl=http://${APP_NAME}:8080"