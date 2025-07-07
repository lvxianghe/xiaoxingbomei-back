# 🐕 小型博美后端服务

基于 Spring Boot 3.4.3 + Spring AI 的智能对话后端服务，支持多种大语言模型和向量化搜索。

## 🏗️ 技术栈

- **框架**: Spring Boot 3.4.3
- **AI集成**: Spring AI 1.0.0
- **数据库**: MySQL 8.0
- **连接池**: HikariCP
- **构建工具**: Maven
- **容器化**: Docker + Docker Compose

## 🚀 Docker 快速部署

### 系统要求
- Docker 20.0+
- Docker Compose 2.0+
- 可用端口：28928、3308

### 部署步骤

1. **启动后端服务**
   ```bash
   # 进入后端目录
   cd xiaoxingbomei-back
   
   # 构建并启动服务（后端 + MySQL）
   docker-compose -f docker-compose-backend.yml up --build -d
   ```

2. **验证服务状态**
   ```bash
   # 查看容器状态
   docker-compose -f docker-compose-backend.yml ps
   
   # 查看后端日志
   docker-compose -f docker-compose-backend.yml logs -f backend
   
   # 健康检查
   curl http://localhost:28928/actuator/health
   ```

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端API | 28928 | Spring Boot 应用 |
| MySQL | 3308 | 数据库外部访问端口 |

### 环境配置

- **开发环境**: `application-dev.yml`
- **Docker环境**: `application-docker.yml`
- **生产环境**: 通过 `SPRING_PROFILES_ACTIVE` 环境变量控制

## 🔧 开发环境配置

### 本地开发

1. **配置数据库**
   ```sql
   # 创建数据库
   CREATE DATABASE ysx_app;
   
   # 配置用户权限
   GRANT ALL PRIVILEGES ON ysx_app.* TO 'root'@'localhost';
   ```

2. **修改配置文件**
   编辑 `src/main/resources/application-dev.yml`：
   ```yaml
   datasource:
     localhost:
       jdbc-url: jdbc:mysql://localhost:3306/ysx-app
       username: root
       password: your_password
   ```

3. **启动应用**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### AI模型配置

支持的模型提供商：
- **OpenAI**: GPT-4o、GPT-3.5-turbo
- **Grok**: Grok-3
- **Ollama**: 本地部署模型

配置示例：
```yaml
ai:
  openai:
    gpt-4o:
      base-url: https://api.openai.com
      api-key: sk-xxx
      model: gpt-4o
      temperature: 0.7
```

## 🗂️ 项目结构

```
xiaoxingbomei-back/
├── llm-server/                 # 主服务模块
│   ├── src/main/java/
│   │   ├── config/            # 配置类
│   │   ├── controller/        # REST API
│   │   ├── service/           # 业务逻辑
│   │   └── dao/               # 数据访问层
│   └── src/main/resources/
│       ├── application-dev.yml
│       └── application-docker.yml
├── llm-common/                # 公共模块
├── llm-client/                # 客户端模块
├── docker-compose-backend.yml # Docker 部署配置
└── Dockerfile                 # 容器构建文件
```

## 📋 API 文档

### 健康检查
```bash
GET /actuator/health
```

### 聊天接口
```bash
POST /chat
Content-Type: application/json

{
  "message": "你好",
  "model": "gpt-4o"
}
```

## 🔧 运维管理

### 服务管理
```bash
# 停止服务
docker-compose -f docker-compose-backend.yml down

# 重启服务
docker-compose -f docker-compose-backend.yml restart

# 查看日志
docker-compose -f docker-compose-backend.yml logs -f

# 进入容器
docker exec -it xiaoxingbomei-backend bash
```

### 数据库管理
```bash
# 连接数据库
mysql -h localhost -P 3308 -u root -p

# 数据库备份
docker exec xiaoxingbomei-backend-mysql mysqldump -u root -p ysx-app > backup.sql

# 数据库恢复
docker exec -i xiaoxingbomei-backend-mysql mysql -u root -p ysx-app < backup.sql
```

## 🐛 常见问题

**Q: 容器启动失败，报数据源配置错误？**
A: 确保MySQL容器完全启动，检查 `application-docker.yml` 中的数据源配置。

**Q: 健康检查失败？**
A: 检查应用日志，确认数据库连接和依赖服务状态。

**Q: 端口冲突？**
A: 修改 `docker-compose-backend.yml` 中的端口映射。

## 📝 更新日志

- **v1.0.0**: 初始版本，支持基础聊天功能
- **v1.1.0**: 添加多模型支持
- **v1.2.0**: 优化Docker部署配置

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 项目介绍

作为个人大模型应用开发学习，后端项目
"小型博美"是一个综合性AI服务平台，旨在为用户提供多种AI智能应用服务。

### 技术栈

 - jdk：17
 - springAI 1.0
 - mybatis
 - mysql
 - simple vector
 - log4j2
 - 模型接入：ollama/openai
 - 大模型：deepseek-v3、deepseek-r1、gemini2.5-pro、grok3......

### 部署说明

#### 传统部署方式
1. jdk装好17的
2. maven配好
3. sql中的ddl执行一下(file/sql)
4. 改一下工程中数据源信息（application.yml）
5. 改一下大模型的apikey（application.yml）
6. 先启动llm-server 然后学到mcp最后了再启动llm-client就可以了

#### 🚀 Docker 快速部署

**系统要求**
- Docker 20.0+
- Docker Compose 2.0+
- 可用端口：28928、3308

**部署步骤**

1. **启动后端服务**
   ```bash
   # 进入后端目录
   cd xiaoxingbomei-back
   
   # 构建并启动服务（后端 + MySQL）
   docker-compose -f docker-compose-backend.yml up --build -d
   ```

2. **验证服务状态**
   ```bash
   # 查看容器状态
   docker-compose -f docker-compose-backend.yml ps
   
   # 查看后端日志
   docker-compose -f docker-compose-backend.yml logs -f backend
   
   # 健康检查
   curl http://localhost:28928/actuator/health
   ```

**服务端口**

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端API | 28928 | Spring Boot 应用 |
| MySQL | 3308 | 数据库外部访问端口 |

**服务管理**
```bash
# 停止服务
docker-compose -f docker-compose-backend.yml down

# 重启服务
docker-compose -f docker-compose-backend.yml restart

# 查看日志
docker-compose -f docker-compose-backend.yml logs -f
```

### 效果展示
#### 入口
![image](https://github.com/user-attachments/assets/987ad65d-f246-4e67-9f09-8e491c947a23)

#### 模型管理

![image](https://github.com/user-attachments/assets/d5f1daef-7dcf-4fe3-a3c8-2e49db01b3ab)

#### 对话
![image](https://github.com/user-attachments/assets/9c0d212a-66b3-4cb9-a4a3-d2ecc8e35707)

#### 提示词工程
![image](https://github.com/user-attachments/assets/07a37e9b-0fd4-42fd-bd86-353ef832d91a)
![image](https://github.com/user-attachments/assets/332c2c00-ce5c-416a-a858-5a6f632dbf20)

#### RAG
![4bf044f51d172bb4a5c9388c3574e23](https://github.com/user-attachments/assets/0f468406-0c3a-47d7-8ba9-d27626467c1b)
![1e708390c396fabc61f5f06b4ee166c](https://github.com/user-attachments/assets/c050e26e-165c-4c9f-b27f-dd0379fc63ec)

#### 多模态
![926f6986e159f3c388d9b29701e4bdb](https://github.com/user-attachments/assets/72084cf7-1188-4135-89d8-5cce627eaf40)
![6ed129333c2819bb5f5c47d1de4c4dd](https://github.com/user-attachments/assets/fdeef08b-b457-4790-b5fc-f15364004e7a)


#### FunctionCalling/MCP
![image](https://github.com/user-attachments/assets/c6938bb9-b4df-4430-bbd9-e2be13a83c72)
![5fa2c08e689bd28753072be52b38114](https://github.com/user-attachments/assets/287a3a20-cc95-4a4f-8b75-49198bcc00a6)
![efc60393532ddd11e11f6c6a71a5f7a](https://github.com/user-attachments/assets/634fffdd-3cbf-453d-862c-c91bf42c0cc6)

