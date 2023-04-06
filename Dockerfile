FROM docker.drift.inera.se/intyg/tomcat-java11-base:9

ENV APP_NAME=rehabstod SCRIPT_DEBUG=true

ADD /web/build/libs/*.war $JWS_HOME/webapps/ROOT.war
