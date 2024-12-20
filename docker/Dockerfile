FROM maven:3.8.1-openjdk-17 AS maven-build
## 创建目录，并使用它作为工作目录
RUN mkdir -p /esdemo
WORKDIR /esdemo
#把mvn需要的文件复制到工作文件夹中
COPY pom.xml .
COPY src ./src
# 使用Maven构建项目
RUN mvn -f pom.xml clean package

#开始docker构建
FROM ccr.ccs.tencentyun.com/mmkj_1_20241218/jre17:v1
ARG JAR_FILE=target/esdemo2-0.1.jar
## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

## 应用参数
ENV ARGS=""

## 暴露后端项目的 8080 端口
EXPOSE 8080

## 启动后端项目
CMD java ${JAVA_OPTS} -jar app.jar $ARGS


