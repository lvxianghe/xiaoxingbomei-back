package org.xiaoxingbomei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xiaoxingbomei.config.tools.WeatherTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private WeatherTool weatherTool;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 服务器状态检查
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "MCP Server is running");
        response.put("port", 28928);
        
        return response;
    }

    /**
     * 天气工具接口（用于Client端HTTP降级调用）
     */
    @GetMapping("/weather")
    public Map<String, Object> getWeather(@RequestParam(defaultValue = "北京") String city) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("天气工具调用: {}", city);
            String result = weatherTool.getWeather(city);
            
            response.put("success", true);
            response.put("city", city);
            response.put("weather", result);
            response.put("method", "direct");
            
        } catch (Exception e) {
            log.error("天气工具调用失败", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    /**
     * MCP Server端诊断接口
     */
    @GetMapping("/mcp/diagnosis")
    public Map<String, Object> getMcpServerDiagnosis() {
        log.info("🔍 检查MCP Server端工具注册情况...");
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("timestamp", System.currentTimeMillis());
            result.put("server", "MCP Server");
            result.put("port", 28928);
            
            // 检查syncTools Bean
            try {
                List<?> syncTools = applicationContext.getBean("syncTools", List.class);
                result.put("syncTools_found", true);
                result.put("syncTools_count", syncTools.size());
                result.put("syncTools_class", syncTools.getClass().getName());
                
                if (syncTools.size() > 0) {
                    result.put("first_tool_class", syncTools.get(0).getClass().getName());
                    log.info("✅ syncTools包含 {} 个工具，第一个工具类型: {}", 
                        syncTools.size(), syncTools.get(0).getClass().getName());
                } else {
                    log.warn("⚠️ syncTools Bean存在但为空");
                }
                
            } catch (Exception e) {
                result.put("syncTools_found", false);
                result.put("syncTools_error", e.getMessage());
                log.error("❌ 未找到syncTools Bean", e);
            }
            
            // 检查mcpSyncServer Bean
            try {
                Object mcpSyncServer = applicationContext.getBean("mcpSyncServer");
                result.put("mcpSyncServer_found", true);
                result.put("mcpSyncServer_class", mcpSyncServer.getClass().getName());
                log.info("✅ 发现mcpSyncServer Bean: {}", mcpSyncServer.getClass().getName());
            } catch (Exception e) {
                result.put("mcpSyncServer_found", false);
                result.put("mcpSyncServer_error", e.getMessage());
                log.error("❌ 未找到mcpSyncServer Bean", e);
            }
            
            // 检查WeatherTool Bean
            result.put("weatherTool_available", weatherTool != null);
            if (weatherTool != null) {
                result.put("weatherTool_class", weatherTool.getClass().getName());
                // 测试调用
                try {
                    String testResult = weatherTool.getWeather("诊断测试");
                    result.put("weatherTool_test", "success");
                    result.put("weatherTool_test_result", testResult);
                } catch (Exception e) {
                    result.put("weatherTool_test", "failed");
                    result.put("weatherTool_test_error", e.getMessage());
                }
            }
            
            result.put("diagnosis", "MCP Server端工具注册检查完成");
            
        } catch (Exception e) {
            log.error("MCP Server诊断失败", e);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
} 