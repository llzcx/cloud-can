# 基于openjdk 镜像
FROM openjdk:8
# 将本地文件夹挂在到当前容器
VOLUME /tmp
# 复制文件到容器
ADD oss-0.0.1-SNAPSHOT.jar oss.jar
# 声明需要暴露的端口
EXPOSE 8080
# 中文
ENV LANG C.UTF-8
# 配置容器启动后执行的命令
ENTRYPOINT ["java","-Dfile.encoding=utf-8","-Djava.security.egd=file:/dev/./urandom","-jar","/oss.jar"]

