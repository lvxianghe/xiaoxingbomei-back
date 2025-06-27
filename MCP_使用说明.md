# MCP (Model Context Protocol) 使用说明

## 项目简介
根据文章《MCP的原理-AI时代的USB接口》实现的完整MCP客户端和服务端示例。

## 架构概述

```
┌─────────────────┐    MCP Protocol    ┌─────────────────┐
│   MCP Client    │◄──────────────────►│   MCP Server    │
│   (28929端口)   │    JSON-RPC/SSE    │   (28928端口)   │
│                 │                    │                 │
│ - ChatClient    │                    │ - WeatherTool   │
│ - ToolCallback  │                    │ - @Tool注解     │
│ - 大模型集成    │                    │ - 工具实现      │
└─────────────────┘                    └─────────────────┘
```

## 核心组件

### 1. MCP Server (llm-server)
- **端口**: 28928
- **功能**: 提供工具服务
- **配置**: `application-dev.yml` 中启用MCP Server
- **工具**: `WeatherTool` - 获取天气信息

### 2. MCP Client (llm-client)  
- **端口**: 28929
- **功能**: 连接MCP Server，调用大模型
- **配置**: `application.yml` 中配置MCP Client和OpenAI
- **服务**: `McpDemoService` - 演示MCP调用流程

## 启动步骤

### 1. 启动MCP Server
```bash
cd llm-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

启动成功后可以访问：
- 健康检查: http://localhost:28928/test/ping
- MCP诊断: http://localhost:28928/test/mcp/diagnosis
- 直接调用工具: http://localhost:28928/test/weather?city=北京

### 2. 启动MCP Client
```bash
cd llm-client  
mvn spring-boot:run
```

启动成功后可以访问：
- MCP客户端诊断: http://localhost:28929/mcp/diagnosis
- 获取工具列表: http://localhost:28929/mcp/tools
- 测试MCP连接: http://localhost:28929/mcp/test-connection

## 使用示例

### 1. 自动演示
Client启动后会自动运行演示，查看日志：
```
=== 开始MCP演示 ===
🔧 发现 1 个可用工具:
  - getWeather: 获取天气信息
📝 用户问: 获取北京的天气
🤖 正在调用大模型...
✅ AI回答: 根据查询结果，北京今天天气无敌了，掉下来很多小型博美
=== MCP演示完成 ===
```

### 2. 手动调用
```bash
# POST调用MCP
curl -X POST "http://localhost:28929/mcp/chat" \
  -d "userInput=获取上海的天气"

# 手动触发演示
curl -X POST "http://localhost:28929/mcp/demo" \
  -d "question=今天广州的天气怎么样"
```

### 3. API接口说明

#### MCP Client接口 (28929端口)
| 接口 | 方法 | 说明 |
|------|------|------|
| `/mcp/chat` | POST | 通过MCP调用大模型 |
| `/mcp/demo` | POST | 手动触发MCP演示 |
| `/mcp/tools` | GET | 获取可用工具列表 |
| `/mcp/diagnosis` | GET | MCP客户端诊断 |
| `/mcp/test-connection` | GET | 测试MCP连接 |

#### MCP Server接口 (28928端口)
| 接口 | 方法 | 说明 |
|------|------|------|
| `/test/ping` | GET | 服务健康检查 |
| `/test/weather` | GET | 直接调用天气工具 |
| `/test/mcp/diagnosis` | GET | MCP服务端诊断 |
| `/mcp/sse` | GET | MCP SSE连接端点 |

## 技术实现

### 1. 通信协议
- **JSON-RPC**: 底层通信协议
- **SSE**: 服务端推送事件，支持长连接

### 2. 核心流程
1. **建立连接**: Client通过SSE连接到Server
2. **获取工具**: Client向Server请求tools/list
3. **工具调用**: 大模型选择工具，通过MCP调用
4. **返回结果**: Server执行工具并返回结果

### 3. 关键配置

#### Server端配置
```yaml
spring:
  ai:
    mcp:
      server:
        enabled: true
        sse:
          enabled: true
          endpoint: "/mcp/sse"
```

#### Client端配置  
```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true
        sse:
          connections:
            weather-server:
              url: http://localhost:28928/mcp/sse
```

## 故障排查

### 1. 检查MCP连接
访问诊断接口检查组件状态：
- Server: http://localhost:28928/test/mcp/diagnosis
- Client: http://localhost:28929/mcp/diagnosis

### 2. 常见问题
- **工具未发现**: 检查Server端@Tool注解和Component注解
- **连接失败**: 检查端口配置和网络连通性
- **大模型调用失败**: 检查OpenAI API配置

### 3. 日志查看
```bash
# 查看Server日志
tail -f llm-server/logs/ai.log

# 查看Client日志  
tail -f llm-client/logs/default.log
```

## 扩展开发

### 1. 添加新工具
在Server端创建新的Tool类：
```java
@Component
public class NewTool {
    @Tool(name = "newFunction", description = "新功能描述")
    public String newFunction(String param) {
        // 实现逻辑
        return "结果";
    }
}
```

### 2. 支持更多通信方式
MCP支持STDIO和SSE两种通信方式，可根据需要扩展。

### 3. 集成更多大模型
在Client端配置其他模型提供商（如Ollama等）。

## 文章对照
本实现完全基于文章《MCP的原理-AI时代的USB接口》，实现了：
- ✅ C/S架构设计
- ✅ JSON-RPC通信协议  
- ✅ SSE通信方式
- ✅ 工具动态发现
- ✅ 标准化工具调用
- ✅ 大模型集成

MCP真正实现了AI工具调用的标准化，就像USB统一了硬件接口一样！ 