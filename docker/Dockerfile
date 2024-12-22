# 使用 maven:3.8.1-openjdk-17 作为基础镜像，包含 Maven 和 JDK
FROM maven:3.8.1-openjdk-17 AS maven-build

# 创建目录，并使用它作为工作目录
RUN mkdir -p /esdemo
WORKDIR /esdemo

# 将 Maven 构建需要的文件复制到工作目录
COPY pom.xml .
COPY src ./src

# 使用 Maven 构建项目
RUN mvn clean package

# 继续构建容器（基于 maven:3.8.1-openjdk-17）
FROM maven:3.8.1-openjdk-17

# 设置工作目录
WORKDIR /u01

# 复制配置文件
COPY config/filebeat.yml .

# 配置 filebeat
RUN curl -o filebeat.tar.gz https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-8.14.1-linux-x86_64.tar.gz \
    && tar zxf filebeat.tar.gz \
    && mv filebeat-8.14.1-linux-x86_64 filebeat \
    && rm filebeat.tar.gz

# 从 Maven 构建阶段复制构建好的 JAR 文件
COPY --from=maven-build /esdemo/target/esdemo2-0.1.jar ./esdemo.jar

# 设置时区环境变量
ENV TZ=Asia/Shanghai

# 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

# 设置应用参数
ENV ARGS=""

# 暴露 8080 端口
EXPOSE 8080

# 启动 filebeat 和 Java 应用程序
CMD ["/bin/bash", "-c", "nohup /u01/filebeat/filebeat -e -c /u01/filebeat.yml & exec java ${JAVA_OPTS} -jar /u01/esdemo.jar $ARGS"]
