# MCP 快速启动指南

## 🚀 正确的启动顺序

### 步骤1: 启动 MCP Server
```bash
# 在第一个终端窗口
cd llm-server
mvn spring-boot:run
```

等待看到以下日志：
```
🔧 MCP Server 提供工具:
   ├─ WeatherTool: 天气查询工具
   ├─ CoffeeTools: 咖啡订购工具
   ├─ DatabaseTool: 数据库工具
   └─ SystemTool: 系统工具
🌐 MCP Server启动完成: http://localhost:28928
```

### 步骤2: 启动 MCP Client
```bash
# 在第二个终端窗口
cd llm-client
mvn spring-boot:run
```

成功启动后会看到：
```
🚀 MCP Client 管理器开始初始化...
📋 注册MCP Server配置...
📋 已注册MCP Server: weather-server - http://127.0.0.1:28928
🔍 尝试连接MCP Servers并发现工具...
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
   ... (更多工具)
📈 总计: 12 个工具可供AI调用
🌐 MCP Client端点: http://localhost:28929
================================================================================
🔧 发现 12 个可用工具，开始测试...
🔍 测试工具调用 - 问题: 今天杭州的天气如何？
🤖 AI回答: 杭州今天天气无敌了，掉下来很多小型博美
```

## ❌ 如果启动顺序错误

如果先启动Client，会看到：
```
⚠️ 连接MCP Server失败: weather-server - MCP Server未启动或无法访问
💡 提示: 请确保MCP Server已启动 (http://127.0.0.1:28928)
⚠️ 没有连接到任何MCP Server，工具调用将不可用
💡 请启动MCP Server后使用 /mcp/tools/refresh 刷新连接
============================================================
📖 启动说明:
1. 首先启动 MCP Server: cd llm-server && mvn spring-boot:run
2. 然后重新启动 MCP Client 或调用刷新接口
3. 刷新接口: POST http://localhost:28929/mcp/tools/refresh
============================================================
⚠️ 当前没有可用的工具，跳过测试
💡 请启动MCP Server后重试
```

## 🔄 如何修复启动顺序问题

### 方法1: 重新启动Client
1. 先启动MCP Server
2. 停止MCP Client (Ctrl+C)
3. 重新启动MCP Client

### 方法2: 使用刷新接口
1. 启动MCP Server
2. 调用刷新接口：
```bash
curl -X POST http://localhost:28929/mcp/tools/refresh
```

## 🧪 测试接口

### 检查连接状态
```bash
curl http://localhost:28929/mcp/health
```

### 查看发现的工具
```bash
curl http://localhost:28929/mcp/tools
```

### 查看MCP Server状态
```bash
curl http://localhost:28929/mcp/servers
```

## 💡 常见问题

### Q: Client启动报错 "连接被拒绝"
**A:** MCP Server未启动，请先启动Server端

### Q: 工具调用失败
**A:** 检查两个服务是否都正常启动，使用健康检查接口验证

### Q: 如何添加新的MCP Server
**A:** 在Client的`application.yml`中添加新的连接配置

## 🎯 成功标志

- **MCP Server**: 看到工具列表打印
- **MCP Client**: 看到工具发现成功和测试调用
- **工具调用**: AI能够成功调用工具并返回结果 