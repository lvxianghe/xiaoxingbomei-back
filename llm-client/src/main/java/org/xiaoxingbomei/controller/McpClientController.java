package org.xiaoxingbomei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;
import org.xiaoxingbomei.config.McpClientManager;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP Client控制器 - 管理MCP连接和工具
 */
@Slf4j
@RestController
@RequestMapping("/mcp")
public class McpClientController {

    @Autowired
    private McpClientManager mcpClientManager;

    /**
     * MCP Client健康检查接口
     * GET /mcp/health
     */
    @GetMapping("/health")
    public GlobalResponse getHealth() {
        log.info("🩺 MCP Client健康检查请求");
        
        try {
            Map<String, Object> healthStatus = mcpClientManager.getHealthStatus();
            healthStatus.put("status", "healthy");
            healthStatus.put("timestamp", System.currentTimeMillis());
            healthStatus.put("client", "MCP Client v1.0");
            
            log.info("✅ MCP Client健康检查完成 - 连接服务器: {}, 工具总数: {}", 
                healthStatus.get("connectedServers"), 
                healthStatus.get("totalTools"));
            
            return GlobalResponse.success(healthStatus, "MCP Client服务健康");
        } catch (Exception e) {
            log.error("❌ MCP Client健康检查失败", e);
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("status", "unhealthy");
            errorStatus.put("error", e.getMessage());
            errorStatus.put("timestamp", System.currentTimeMillis());
            
            return GlobalResponse.error("MCP Client服务异常: " + e.getMessage());
        }
    }

    /**
     * 获取所有MCP Server连接状态
     * GET /mcp/servers
     */
    @GetMapping("/servers")
    public GlobalResponse getServers() {
        log.info("🔗 获取MCP Server连接状态请求");
        
        try {
            Map<String, Object> healthStatus = mcpClientManager.getHealthStatus();
            Map<String, Object> response = new HashMap<>();
            response.put("totalServers", healthStatus.get("totalServers"));
            response.put("connectedServers", healthStatus.get("connectedServers"));
            response.put("servers", healthStatus.get("servers"));
            response.put("timestamp", System.currentTimeMillis());
            
            return GlobalResponse.success(response, "获取MCP Server连接状态成功");
        } catch (Exception e) {
            log.error("❌ 获取MCP Server连接状态失败", e);
            return GlobalResponse.error("获取连接状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取从MCP Servers发现的工具列表
     * GET /mcp/tools
     */
    @GetMapping("/tools")
    public GlobalResponse getTools(@RequestParam(value = "server", required = false) String serverName) {
        log.info("🔧 获取MCP工具列表请求 - 服务器: {}", serverName);
        
        try {
            if (serverName != null && !serverName.trim().isEmpty()) {
                var tools = mcpClientManager.getToolsByServer(serverName);
                log.info("📋 返回服务器 {} 的工具列表 - 数量: {}", serverName, tools.size());
                return GlobalResponse.success(tools, "获取指定服务器工具列表成功");
            } else {
                var allTools = mcpClientManager.getAllTools();
                log.info("📋 返回全部工具列表 - 数量: {}", allTools.size());
                return GlobalResponse.success(allTools, "获取全部工具列表成功");
            }
        } catch (Exception e) {
            log.error("❌ 获取工具列表失败", e);
            return GlobalResponse.error("获取工具列表失败: " + e.getMessage());
        }
    }

    /**
     * 重新连接指定MCP Server
     * POST /mcp/servers/{serverName}/reconnect
     */
    @PostMapping("/servers/{serverName}/reconnect")
    public GlobalResponse reconnectServer(@PathVariable String serverName) {
        log.info("🔄 重新连接MCP Server请求 - 服务器: {}", serverName);
        
        try {
            boolean success = mcpClientManager.reconnectServer(serverName);
            if (success) {
                int toolCount = mcpClientManager.getToolsByServer(serverName).size();
                Map<String, Object> result = new HashMap<>();
                result.put("serverName", serverName);
                result.put("connected", true);
                result.put("toolCount", toolCount);
                result.put("timestamp", System.currentTimeMillis());
                
                return GlobalResponse.success(result, "重新连接MCP Server成功");
            } else {
                return GlobalResponse.error("重新连接MCP Server失败，服务器不存在: " + serverName);
            }
        } catch (Exception e) {
            log.error("❌ 重新连接MCP Server失败 - 服务器: {}", serverName, e);
            return GlobalResponse.error("重新连接失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定MCP Server的连接状态
     * GET /mcp/servers/{serverName}/status
     */
    @GetMapping("/servers/{serverName}/status")
    public GlobalResponse getServerStatus(@PathVariable String serverName) {
        log.info("📊 获取MCP Server状态请求 - 服务器: {}", serverName);
        
        try {
            boolean connected = mcpClientManager.isServerConnected(serverName);
            int toolCount = mcpClientManager.getToolsByServer(serverName).size();
            
            Map<String, Object> status = new HashMap<>();
            status.put("serverName", serverName);
            status.put("connected", connected);
            status.put("toolCount", toolCount);
            status.put("timestamp", System.currentTimeMillis());
            
            return GlobalResponse.success(status, "获取MCP Server状态成功");
        } catch (Exception e) {
            log.error("❌ 获取MCP Server状态失败 - 服务器: {}", serverName, e);
            return GlobalResponse.error("获取服务器状态失败: " + e.getMessage());
        }
    }

    /**
     * 刷新所有MCP Server的工具发现
     * POST /mcp/tools/refresh
     */
    @PostMapping("/tools/refresh")
    public GlobalResponse refreshTools() {
        log.info("🔄 刷新MCP工具发现请求");
        
        try {
            mcpClientManager.discoverToolsFromServers();
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalTools", mcpClientManager.getAllTools().size());
            result.put("healthStatus", mcpClientManager.getHealthStatus());
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ 工具发现刷新完成 - 总工具数: {}", result.get("totalTools"));
            return GlobalResponse.success(result, "刷新MCP工具发现成功");
        } catch (Exception e) {
            log.error("❌ 刷新MCP工具发现失败", e);
            return GlobalResponse.error("刷新工具发现失败: " + e.getMessage());
        }
    }

    /**
     * MCP Client服务信息接口
     * GET /mcp/info
     */
    @GetMapping("/info")
    public GlobalResponse getInfo() {
        log.info("ℹ️ 获取MCP Client服务信息请求");
        
        try {
            Map<String, Object> info = new HashMap<>();
            info.put("name", "MCP Client");
            info.put("version", "1.0.0");
            info.put("description", "Model Context Protocol Client - 小星博美AI工具客户端");
            info.put("port", 28929);
            info.put("endpoints", Map.of(
                "health", "/mcp/health",
                "servers", "/mcp/servers",
                "tools", "/mcp/tools",
                "info", "/mcp/info"
            ));
            info.put("features", new String[]{
                "多MCP Server连接管理",
                "工具自动发现",
                "连接状态监控",
                "工具统一调用"
            });
            info.put("timestamp", System.currentTimeMillis());
            
            return GlobalResponse.success(info, "获取MCP Client服务信息成功");
        } catch (Exception e) {
            log.error("❌ 获取MCP Client服务信息失败", e);
            return GlobalResponse.error("获取MCP Client服务信息失败: " + e.getMessage());
        }
    }
} 