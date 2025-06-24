package org.xiaoxingbomei.controller;

import lombok.extern.slf4j.Slf4j;
// 移除McpSchema导入，使用Map替代
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xiaoxingbomei.service.McpClientService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mcp")
public class McpTestController {

    @Autowired
    private McpClientService mcpClientService;

    /**
     * 测试MCP连接
     */
    @GetMapping("/test-connection")
    public Map<String, Object> testConnection() {
        log.info("测试MCP连接");
        Map<String, Object> result = new HashMap<>();
        
        boolean connected = mcpClientService.testConnection();
        result.put("connected", connected);
        result.put("message", connected ? "MCP连接正常" : "MCP连接失败");
        
        return result;
    }

    /**
     * 获取可用工具列表
     */
    @GetMapping("/tools")
    public Map<String, Object> getTools() {
        log.info("获取MCP工具列表");
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> tools = mcpClientService.getAvailableTools();
            result.put("success", true);
            result.put("tools", tools);
            result.put("count", tools.size());
        } catch (Exception e) {
            log.error("获取工具列表失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 调用天气工具
     */
    @GetMapping("/weather")
    public Map<String, Object> getWeather(@RequestParam(defaultValue = "北京") String city) {
        log.info("调用天气工具, 城市: {}", city);
        Map<String, Object> result = new HashMap<>();
        
        try {
            String weather = mcpClientService.getWeather(city);
            result.put("success", true);
            result.put("city", city);
            result.put("weather", weather);
        } catch (Exception e) {
            log.error("调用天气工具失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * MCP状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        
        boolean connected = mcpClientService.testConnection();
        List<Map<String, Object>> tools = mcpClientService.getAvailableTools();
        
        result.put("connected", connected);
        result.put("toolCount", tools.size());
        result.put("tools", tools);
        
        return result;
    }
} 