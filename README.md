# 小型博美AI 后端

## 项目介绍

作为个人大模型应用开发学习，后端项目
”小型博美“是一个综合性AI服务平台，旨在为用户提供多种AI智能应用服务。

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
1. jdk装好17的
2. maven配好
3. sql中的ddl执行一下(file/sql)
4. 改一下工程中数据源信息（application.yml）
5. 改一下大模型的apikey（application.yml）
6. 先启动llm-server 然后学到mcp最后了再启动llm-client就可以了


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

