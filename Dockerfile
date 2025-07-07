# 小型博美后端SpringBoot项目Dockerfile
# 使用官方OpenJDK镜像
FROM openjdk:17-jdk-slim

# 安装Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制源代码
COPY . .

# 构建项目（一次性下载依赖并构建）
RUN mvn clean package -DskipTests -B

# 暴露端口
EXPOSE 28928

# 设置启动命令
CMD ["java", "-jar", "llm-server/target/llm-server-1.0-SNAPSHOT.jar", "--spring.profiles.active=docker"] 