package org.xiaoxingbomei.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP Client 管理器 - 连接和管理多个MCP Server
 */
@Slf4j
@Component
public class McpClientManager {

    @Value("${spring.ai.mcp.client.sse.connections.weather-server.url:http://127.0.0.1:28928}")
    private String mcpServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    
    // MCP Server连接注册表：服务名 -> 服务信息
    private final Map<String, McpServerInfo> serverRegistry = new ConcurrentHashMap<>();
    
    // 工具注册表：工具名 -> 工具信息（来自哪个server）
    private final Map<String, McpToolInfo> toolRegistry = new ConcurrentHashMap<>();
    
    // 服务器状态：服务名 -> 是否可用
    private final Map<String, Boolean> serverStatus = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeMcpClient() {
        log.info("🚀 MCP Client 管理器开始初始化...");
        log.info("📋 注册MCP Server配置...");
        
        // 注册默认的MCP Server
        registerMcpServer("weather-server", mcpServerUrl, "默认MCP工具服务器");
        
        log.info("🔍 尝试连接MCP Servers并发现工具...");
        // 发现并注册所有工具
        discoverToolsFromServers();
        
        log.info("✅ MCP Client 管理器初始化完成！");
        printMcpSummary();
        
        // 给出启动提示
        int connectedServers = (int) serverStatus.values().stream().filter(status -> status).count();
        if (connectedServers == 0) {
            log.info("\n" + "=".repeat(60));
            log.info("📖 启动说明:");
            log.info("1. 首先启动 MCP Server: cd llm-server && mvn spring-boot:run");
            log.info("2. 然后重新启动 MCP Client 或调用刷新接口");
            log.info("3. 刷新接口: POST http://localhost:28929/mcp/tools/refresh");
            log.info("=".repeat(60));
        }
    }

    /**
     * 注册MCP Server
     */
    public void registerMcpServer(String serverName, String serverUrl, String description) {
        McpServerInfo serverInfo = new McpServerInfo(serverName, serverUrl, description);
        serverRegistry.put(serverName, serverInfo);
        serverStatus.put(serverName, false); // 初始状态为未连接
        
        log.info("📋 已注册MCP Server: {} - {}", serverName, serverUrl);
    }

    /**
     * 从所有注册的服务器发现工具
     */
    public void discoverToolsFromServers() {
        log.info("🔍 开始从MCP Servers发现工具...");
        
        for (Map.Entry<String, McpServerInfo> entry : serverRegistry.entrySet()) {
            String serverName = entry.getKey();
            McpServerInfo serverInfo = entry.getValue();
            
            try {
                discoverToolsFromServer(serverName, serverInfo);
                serverStatus.put(serverName, true);
            } catch (Exception e) {
                log.warn("⚠️ 连接MCP Server失败: {} - {}", serverName, e.getMessage());
                log.info("💡 提示: 请确保MCP Server已启动 ({})", serverInfo.getUrl());
                serverStatus.put(serverName, false);
            }
        }
        
        int connectedServers = (int) serverStatus.values().stream().filter(status -> status).count();
        if (connectedServers == 0) {
            log.warn("⚠️ 没有连接到任何MCP Server，工具调用将不可用");
            log.info("💡 请启动MCP Server后使用 /mcp/tools/refresh 刷新连接");
        }
    }

    /**
     * 从单个服务器发现工具
     */
    private void discoverToolsFromServer(String serverName, McpServerInfo serverInfo) {
        log.info("📡 正在连接MCP Server: {} ({})", serverName, serverInfo.getUrl());
        
        try {
            // 调用MCP Server的工具列表接口
            String toolsUrl = serverInfo.getUrl() + "/mcp/tools";
            Map<String, Object> response = restTemplate.getForObject(toolsUrl, Map.class);
            
            if (response == null) {
                log.warn("⚠️ MCP Server {} 无响应，可能服务未启动", serverName);
                return;
            }
            
            // 检查响应是否成功 - 兼容不同的响应格式
            Object successObj = response.get("success");
            Object codeObj = response.get("code");
            
            boolean isSuccess = false;
            if (successObj instanceof Boolean && ((Boolean) successObj)) {
                isSuccess = true;
            } else if (codeObj != null) {
                // 兼容字符串和整数类型的code
                String codeStr = codeObj.toString();
                if ("200".equals(codeStr)) {
                    isSuccess = true;
                }
            } else if (response.containsKey("data") && response.get("data") != null) {
                // 如果有data字段且不为null，也认为是成功的
                isSuccess = true;
            }
            
            if (!isSuccess) {
                log.warn("⚠️ MCP Server {} 返回异常响应: {}", serverName, response);
                return;
            }
            
            Object dataObj = response.get("data");
            if (!(dataObj instanceof Map)) {
                log.warn("⚠️ MCP Server {} 返回的data字段格式错误", serverName);
                return;
            }
            
            Map<String, Object> data = (Map<String, Object>) dataObj;
            Object toolsObj = data.get("tools");
            if (!(toolsObj instanceof List)) {
                log.warn("⚠️ MCP Server {} 返回的tools字段格式错误", serverName);
                return;
            }
            
            List<Map<String, Object>> tools = (List<Map<String, Object>>) toolsObj;
            int toolCount = 0;
            
            for (Map<String, Object> tool : tools) {
                String className = (String) tool.get("className");
                Object methodsObj = tool.get("methods");
                
                if (!(methodsObj instanceof List)) {
                    continue;
                }
                
                List<Map<String, Object>> methods = (List<Map<String, Object>>) methodsObj;
                
                for (Map<String, Object> method : methods) {
                    String toolName = (String) method.get("toolName");
                    String description = (String) method.get("description");
                    
                    if (toolName != null && description != null) {
                        McpToolInfo toolInfo = new McpToolInfo(
                            toolName, 
                            description, 
                            className != null ? className : "Unknown",
                            serverName,
                            serverInfo.getUrl()
                        );
                        
                        toolRegistry.put(toolName, toolInfo);
                        toolCount++;
                    }
                }
            }
            
            log.info("✅ 从 {} 发现 {} 个工具", serverName, toolCount);
            serverInfo.setToolCount(toolCount);
            
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.debug("🔌 连接MCP Server {} 失败: 连接被拒绝", serverName);
            throw new RuntimeException("MCP Server未启动或无法访问", e);
        } catch (Exception e) {
            log.debug("🔌 从MCP Server {} 获取工具失败: {}", serverName, e.getMessage());
            throw e;
        }
    }

    /**
     * 获取所有可用工具
     */
    public List<McpToolInfo> getAllTools() {
        return new ArrayList<>(toolRegistry.values());
    }

    /**
     * 获取指定服务器的工具
     */
    public List<McpToolInfo> getToolsByServer(String serverName) {
        return toolRegistry.values().stream()
                .filter(tool -> tool.getServerName().equals(serverName))
                .toList();
    }

    /**
     * 获取MCP Client健康状态
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("totalServers", serverRegistry.size());
        health.put("connectedServers", serverStatus.values().stream().mapToInt(status -> status ? 1 : 0).sum());
        health.put("totalTools", toolRegistry.size());
        
        Map<String, Object> serverDetails = new HashMap<>();
        for (String serverName : serverRegistry.keySet()) {
            Map<String, Object> details = new HashMap<>();
            details.put("connected", serverStatus.get(serverName));
            details.put("url", serverRegistry.get(serverName).getUrl());
            details.put("toolCount", getToolsByServer(serverName).size());
            serverDetails.put(serverName, details);
        }
        health.put("servers", serverDetails);
        
        return health;
    }

    /**
     * 检查服务器连接状态
     */
    public boolean isServerConnected(String serverName) {
        return serverStatus.getOrDefault(serverName, false);
    }

    /**
     * 重新连接指定服务器
     */
    public boolean reconnectServer(String serverName) {
        McpServerInfo serverInfo = serverRegistry.get(serverName);
        if (serverInfo == null) {
            return false;
        }
        
        try {
            // 清除该服务器的工具
            toolRegistry.entrySet().removeIf(entry -> entry.getValue().getServerName().equals(serverName));
            
            // 重新发现工具
            discoverToolsFromServer(serverName, serverInfo);
            serverStatus.put(serverName, true);
            
            log.info("✅ 重新连接MCP Server成功: {}", serverName);
            return true;
        } catch (Exception e) {
            log.error("❌ 重新连接MCP Server失败: {}", serverName, e);
            serverStatus.put(serverName, false);
            return false;
        }
    }

    /**
     * 打印MCP摘要
     */
    private void printMcpSummary() {
        log.info("\n" + "=".repeat(80));
        log.info("🎯 MCP Client 连接摘要");
        log.info("=".repeat(80));
        log.info("📊 服务器统计: 总共 {} 个MCP Server", serverRegistry.size());
        
        for (Map.Entry<String, McpServerInfo> entry : serverRegistry.entrySet()) {
            String serverName = entry.getKey();
            McpServerInfo serverInfo = entry.getValue();
            boolean connected = serverStatus.get(serverName);
            int toolCount = getToolsByServer(serverName).size();
            
            log.info("🔗 {} ({}): {} - {} 个工具", 
                serverName, 
                connected ? "✅连接" : "❌断开",
                serverInfo.getUrl(),
                toolCount);
                
            if (connected && toolCount > 0) {
                getToolsByServer(serverName).forEach(tool -> 
                    log.info("   └─ @Tool(name=\"{}\") - {}", tool.getToolName(), tool.getDescription())
                );
            }
        }
        
        log.info("📈 总计: {} 个工具可供AI调用", toolRegistry.size());
        log.info("🌐 MCP Client端点: http://localhost:28929");
        log.info("=".repeat(80));
    }

    // 内部类：MCP Server信息
    public static class McpServerInfo {
        private final String name;
        private final String url;
        private final String description;
        private int toolCount = 0;

        public McpServerInfo(String name, String url, String description) {
            this.name = name;
            this.url = url;
            this.description = description;
        }

        // Getters and Setters
        public String getName() { return name; }
        public String getUrl() { return url; }
        public String getDescription() { return description; }
        public int getToolCount() { return toolCount; }
        public void setToolCount(int toolCount) { this.toolCount = toolCount; }
    }

    // 内部类：MCP工具信息
    public static class McpToolInfo {
        private final String toolName;
        private final String description;
        private final String className;
        private final String serverName;
        private final String serverUrl;

        public McpToolInfo(String toolName, String description, String className, String serverName, String serverUrl) {
            this.toolName = toolName;
            this.description = description;
            this.className = className;
            this.serverName = serverName;
            this.serverUrl = serverUrl;
        }

        // Getters
        public String getToolName() { return toolName; }
        public String getDescription() { return description; }
        public String getClassName() { return className; }
        public String getServerName() { return serverName; }
        public String getServerUrl() { return serverUrl; }
    }
} 