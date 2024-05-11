# 使用官方Java 8镜像作为基础镜像
FROM openjdk:8-jdk-alpine
 
# 复制本地的Java应用到容器内
COPY ./weile-flowable-0.0.1-SNAPSHOT.jar /usr/app/my-app.jar
 
# 设置工作目录
WORKDIR /usr/app

EXPOSE 8080
 
# 指定容器启动时运行的命令
CMD ["java", "-jar", "my-app.jar"]
