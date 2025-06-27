# MCP 工具管理系统

## 🎯 功能概述

基于Spring AI实现的MCP（Model Context Protocol）工具管理系统，支持：

- ✅ **工具配置中心化**：统一管理所有AI工具
- ✅ **动态工具加载**：运行时启用/禁用工具
- ✅ **健康检查**：实时监控工具状态
- ✅ **工具列表API**：获取可用工具清单
- ✅ **启动信息打印**：显示从MCP Server获取的工具

## 🏗️ 架构设计

```
MCP Client (端口: 28929)          MCP Server (端口: 28928)
├── McpClientManager               ├── McpServerController
│   ├── 连接多个MCP Server          │   ├── /mcp/tools (提供工具列表)
│   ├── 工具自动发现                │   └── /mcp/info (服务信息)
│   ├── 连接状态监控                └── 工具类
│   └── 工具统一管理                    ├── WeatherTool
├── McpClientController                 ├── CoffeeTools
│   ├── /mcp/health                     ├── DatabaseTool
│   ├── /mcp/servers                    └── SystemTool
│   ├── /mcp/tools                 
│   └── /mcp/info                  
└── ChatClient集成                 
    └── 统一调用所有发现的工具
```

## 🔧 可用工具

### 1. WeatherTool - 天气查询工具
```java
@Tool(name = "getWeather", description = "获取某个城市的天气信息")
public String getWeather(String city)
```

### 2. CoffeeTools - 咖啡订购工具
```java
@Tool(name = "getCoffeeMenu", description = "获取咖啡菜单")
@Tool(name = "createCoffeeOrder", description = "创建咖啡订单")
// ... 更多方法
```

### 3. DatabaseTool - 数据库工具
```java
@Tool(name = "queryUser", description = "根据用户ID查询用户信息")
@Tool(name = "getUserCount", description = "获取数据库中的用户总数")
```

### 4. SystemTool - 系统工具
```java
@Tool(name = "getCurrentTime", description = "获取当前系统时间")
@Tool(name = "generateRandomNumber", description = "生成指定范围内的随机数")
@Tool(name = "getSystemInfo", description = "获取系统基本信息")
```

## 🚀 启动步骤

### 1. 启动MCP Server
```bash
cd llm-server
mvn spring-boot:run
```

启动日志会显示：
```
🔧 MCP Server 提供工具:
   ├─ WeatherTool: 天气查询工具
   ├─ CoffeeTools: 咖啡订购工具
   ├─ DatabaseTool: 数据库工具
   └─ SystemTool: 系统工具
🌐 MCP Server启动完成: http://localhost:28928
```

### 2. 启动MCP Client
```bash
cd llm-client
mvn spring-boot:run
```

启动日志会显示：
```
🚀 MCP Client 管理器开始初始化...
📋 已注册MCP Server: weather-server - http://127.0.0.1:28928
🔍 开始从MCP Servers发现工具...
📡 正在连接MCP Server: weather-server (http://127.0.0.1:28928)
✅ 从 weather-server 发现 12 个工具
✅ MCP Client 管理器初始化完成！
================================================================================
🎯 MCP Client 连接摘要
================================================================================
📊 服务器统计: 总共 1 个MCP Server
🔗 weather-server (✅连接): http://127.0.0.1:28928 - 12 个工具
   └─ @Tool(name="getWeather") - 获取某个城市的天气信息
   └─ @Tool(name="getCoffeeMenu") - 获取咖啡菜单
   └─ @Tool(name="queryUser") - 根据用户ID查询用户信息
   └─ @Tool(name="getCurrentTime") - 获取当前系统时间
   ... (更多工具)
📈 总计: 12 个工具可供AI调用
🌐 MCP Client端点: http://localhost:28929
================================================================================
```

## 📡 API接口

### MCP Server接口

#### 1. 获取工具列表
```http
GET http://localhost:28928/mcp/tools
```

#### 2. 服务信息
```http
GET http://localhost:28928/mcp/info
```

### MCP Client接口

#### 1. 健康检查
```http
GET http://localhost:28929/mcp/health
```

#### 2. 获取MCP Server连接状态
```http
GET http://localhost:28929/mcp/servers
```

#### 3. 获取发现的工具列表
```http
GET http://localhost:28929/mcp/tools
GET http://localhost:28929/mcp/tools?server=weather-server  # 获取指定服务器的工具
```

#### 4. 重新连接MCP Server
```http
POST http://localhost:28929/mcp/servers/weather-server/reconnect
```

#### 5. 获取服务器状态
```http
GET http://localhost:28929/mcp/servers/weather-server/status
```

#### 6. 刷新工具发现
```http
POST http://localhost:28929/mcp/tools/refresh
```

#### 7. Client服务信息
```http
GET http://localhost:28929/mcp/info
```

## 🔧 如何添加新工具

### 1. 创建工具类
```java
@Slf4j
@Service
@Component
public class MyCustomTool {
    
    @Tool(name = "myFunction", description = "我的自定义功能")
    public String myFunction(String input) {
        log.info("[MyCustomTool] 调用自定义功能: {}", input);
        return "处理结果: " + input;
    }
}
```

### 2. 在McpToolManager中注册
```java
@Autowired
private MyCustomTool myCustomTool;

@PostConstruct
public void initializeTools() {
    // 注册新工具
    registerTool("MyCustomTool", myCustomTool, "我的自定义工具");
    // ... 其他工具
}
```

### 3. 重启服务
重启MCP Server，新工具会自动注册并在启动日志中显示。

## 🎮 测试工具

### 使用Postman测试
1. 导入API接口到Postman
2. 测试健康检查：`GET /mcp/health`
3. 获取工具列表：`GET /mcp/tools`
4. 动态禁用工具：`POST /mcp/tools/WeatherTool/disable`
5. 验证工具状态：`GET /mcp/tools/WeatherTool/status`

### 使用curl测试
```bash
# 健康检查
curl http://localhost:28928/mcp/health

# 获取工具列表
curl http://localhost:28928/mcp/tools

# 禁用工具
curl -X POST http://localhost:28928/mcp/tools/WeatherTool/disable

# 启用工具
curl -X POST http://localhost:28928/mcp/tools/WeatherTool/enable
```

## 💡 优势特点

1. **配置中心化**：所有工具配置集中管理，便于维护
2. **动态管理**：无需重启即可启用/禁用工具
3. **实时监控**：健康检查接口实时监控工具状态
4. **自动发现**：启动时自动扫描和注册@Tool方法
5. **详细日志**：完整的工具调用和状态变更日志
6. **易于扩展**：简单添加新工具类即可自动集成

## 🔮 后续扩展

- [ ] 工具权限控制
- [ ] 工具调用限流
- [ ] 工具性能监控
- [ ] 配置文件动态加载
- [ ] 工具版本管理
- [ ] 分布式工具注册中心 