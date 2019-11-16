# Docker image for springboot file run
# VERSION 1.0.0
# Author: linrol
# 基础镜像使用tomcat
FROM tomcat:7

#打包项目并拷贝到tomcat webapps目录
RUN rm -rf /usr/local/tomcat/webapps/*

#设置时区
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

#配置java与tomcat环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_201
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $JAVA_HOME/bin:$PATH

#开启内部服务端口
EXPOSE 8080

#启动tomcat服务器
#CMD ["/usr/local/tomcat/bin/catalina.sh","run"]