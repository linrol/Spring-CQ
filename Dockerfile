# Docker image for springboot file run
# VERSION 1.0.0
# Author: linrol
# 基础镜像使用java
FROM java:8

ARG PROJ_NAME

COPY target/${PROJ_NAME}.jar ${PROJ_NAME}.jar

ENTRYPOINT java ${JAVA_OPTS} -jar spring-coolq.jar
