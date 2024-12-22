FROM maven:3.8.1-openjdk-17 AS maven-build
## 创建目录，并使用它作为工作目录
RUN mkdir -p /esdemo
WORKDIR /esdemo
#把mvn需要的文件复制到工作文件夹中
COPY pom.xml .
COPY src ./src
# 使用Maven构建项目
RUN mvn  clean package

#开始docker构建
FROM ccr.ccs.tencentyun.com/mmkj_1_20241218/jre17:v1
ARG JAR_FILE=target/esdemo2-0.1.jar
WORKDIR /u01
COPY config/filebeat.yml .

#配置filebeat
RUN cd /usr/local \
    && curl -o filebeat.tar.gz https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-8.14.1-linux-x86_64.tar.gz \
    && tar zxf filebeat.tar.gz \
    && mv filebeat-8.14.1-linux-x86_64 filebeat \
    && rm filebeat.tar.gz

# 从Maven构建阶段复制构建的jar文件
COPY --from=maven-build /esdemo/target/esdemo2-0.1.jar ./esdemo.jar
## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

## 应用参数
ENV ARGS=""

## 暴露后端项目的 8080 端口
EXPOSE 8080



## 启动后端项目
#CMD nohup /usr/local/filebeat/filebeat -e -c /u01.filebeat.yml & && java ${JAVA_OPTS} -jar esdemo.jar $ARGS
CMD ["/bin/bash", "-c", "nohup /usr/local/filebeat/filebeat -e -c /u01/filebeat.yml & exec java ${JAVA_OPTS} -jar esdemo.jar $ARGS"]




