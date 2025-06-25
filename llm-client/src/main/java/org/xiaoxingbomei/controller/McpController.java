package org.xiaoxingbomei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xiaoxingbomei.service.McpClientService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mcp")
public class McpController {

    @Autowired
    private McpClientService mcpClientService;

    /**
     * 获取可用MCP工具列表
     */
    @GetMapping("/tools")
    public Map<String, Object> getTools() {
        log.info("📋 获取MCP工具列表");
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> tools = mcpClientService.getAvailableTools();
            result.put("success", true);
            result.put("tools", tools);
            result.put("count", tools.size());
            result.put("method", "官方MCP协议");
        } catch (Exception e) {
            log.error("获取工具列表失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 官方MCP方式调用天气工具
     */
    @GetMapping("/weather")
    public Map<String, Object> getWeather(@RequestParam(defaultValue = "北京") String city) {
        log.info("🌤️ 官方MCP方式调用天气工具, 城市: {}", city);
        Map<String, Object> result = new HashMap<>();
        
        try {
            String weather = mcpClientService.callWeatherTool(city);
            result.put("success", true);
            result.put("city", city);
            result.put("weather", weather);
            result.put("method", "官方MCP协议");
            
        } catch (Exception e) {
            log.error("调用天气工具失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("method", "MCP调用失败");
        }
        
        return result;
    }

    /**
     * MCP服务状态
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        
        boolean connected = mcpClientService.testConnection();
        List<Map<String, Object>> tools = mcpClientService.getAvailableTools();
        
        result.put("connected", connected);
        result.put("toolCount", tools.size());
        result.put("status", connected ? "MCP连接正常" : "MCP连接失败");
        result.put("protocol", "Spring AI MCP 1.0.0");
        result.put("transport", "SSE");
        
        return result;
    }

    /**
     * MCP协议诊断接口
     */
    @GetMapping("/diagnosis")
    public Map<String, Object> getMcpDiagnosis() {
        log.info("🔍 MCP协议诊断");
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("timestamp", System.currentTimeMillis());
            result.put("protocol", "Spring AI MCP");
            result.put("client_port", 28929);
            result.put("server_port", 28928);
            
            // 检查Provider状态
            boolean hasProvider = mcpClientService.hasToolCallbackProvider();
            result.put("has_provider", hasProvider);
            
            if (hasProvider) {
                // 详细分析Provider
                Map<String, Object> providerDetails = mcpClientService.analyzeProvider();
                result.put("provider_details", providerDetails);
                
                // 检查工具数量
                int toolCount = mcpClientService.getToolCallbackCount();
                result.put("tool_count", toolCount);
                
                if (toolCount > 0) {
                    result.put("diagnosis", "✅ MCP协议工作正常");
                    result.put("recommendation", "可以正常使用MCP工具");
                } else {
                    result.put("diagnosis", "⚠️ MCP连接正常但无工具");
                    result.put("recommendation", "检查Server端工具注册");
                }
            } else {
                result.put("diagnosis", "❌ MCP Provider未配置");
                result.put("recommendation", "检查MCP Client配置和Server连接");
            }
            
        } catch (Exception e) {
            log.error("MCP诊断失败", e);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
} 