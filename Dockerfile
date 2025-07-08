# 小型博美后端SpringBoot项目Dockerfile
# 多阶段构建：构建阶段使用Maven镜像，运行阶段使用JDK镜像

# 构建阶段 - 使用验证过的Maven镜像
FROM maven:3.8-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

# 复制pom.xml文件，先下载依赖（利用Docker缓存）
COPY pom.xml .
COPY llm-server/pom.xml llm-server/
COPY llm-common/pom.xml llm-common/
COPY llm-client/pom.xml llm-client/

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY . .

# 构建项目
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用验证过的JDK镜像
FROM openjdk:17-jdk-slim AS runtime

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=build /app/llm-server/target/llm-server-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 28928

# 设置启动命令
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=docker"] 