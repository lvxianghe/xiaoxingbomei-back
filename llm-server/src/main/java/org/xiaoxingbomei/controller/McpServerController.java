package org.xiaoxingbomei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;
import org.xiaoxingbomei.config.tools.CoffeeTools;
import org.xiaoxingbomei.config.tools.DatabaseTool;
import org.xiaoxingbomei.config.tools.SystemTool;
import org.xiaoxingbomei.config.tools.WeatherTool;

import java.lang.reflect.Method;
import java.util.*;

/**
 * MCP Server控制器 - 提供工具信息
 */
@Slf4j
@RestController
@RequestMapping("/mcp")
public class McpServerController {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 获取MCP Server提供的工具列表
     * GET /mcp/tools
     */
    @GetMapping("/tools")
    public GlobalResponse getServerTools() {
        log.info("🔧 MCP Server 收到工具列表请求");
        
        try {
            List<Map<String, Object>> tools = new ArrayList<>();
            
            // 扫描所有工具类
            Map<String, Object> toolBeans = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Service.class);
            
            for (Map.Entry<String, Object> entry : toolBeans.entrySet()) {
                Object bean = entry.getValue();
                if (isToolClass(bean)) {
                    Map<String, Object> toolInfo = scanToolClass(bean);
                    if (!((List<?>) toolInfo.get("methods")).isEmpty()) {
                        tools.add(toolInfo);
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("serverName", "MCP Server");
            response.put("serverUrl", "http://localhost:28928");
            response.put("toolCount", tools.size());
            response.put("tools", tools);
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ 返回 {} 个工具类信息", tools.size());
            return GlobalResponse.success(response, "获取MCP Server工具列表成功");
            
        } catch (Exception e) {
            log.error("❌ 获取工具列表失败", e);
            return GlobalResponse.error("获取工具列表失败: " + e.getMessage());
        }
    }

    /**
     * MCP Server基本信息
     * GET /mcp/info
     */
    @GetMapping("/info")
    public GlobalResponse getServerInfo() {
        log.info("ℹ️ MCP Server 信息请求");
        
        Map<String, Object> info = new HashMap<>();
        info.put("name", "MCP Server");
        info.put("version", "1.0.0");
        info.put("description", "Model Context Protocol Server - 小星博美AI工具服务");
        info.put("port", 28928);
        info.put("endpoints", Map.of(
            "tools", "/mcp/tools",
            "info", "/mcp/info"
        ));
        info.put("capabilities", new String[]{
            "工具提供",
            "SSE通信",
            "JSON-RPC"
        });
        info.put("timestamp", System.currentTimeMillis());
        
        return GlobalResponse.success(info, "获取MCP Server信息成功");
    }

    /**
     * 检查是否为工具类
     */
    private boolean isToolClass(Object bean) {
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Tool.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描工具类的方法
     */
    private Map<String, Object> scanToolClass(Object bean) {
        Map<String, Object> toolInfo = new HashMap<>();
        Class<?> clazz = bean.getClass();
        
        toolInfo.put("className", clazz.getSimpleName());
        toolInfo.put("packageName", clazz.getPackage().getName());
        
        List<Map<String, Object>> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Tool.class)) {
                Tool toolAnnotation = method.getAnnotation(Tool.class);
                Map<String, Object> methodInfo = new HashMap<>();
                methodInfo.put("toolName", toolAnnotation.name());
                methodInfo.put("description", toolAnnotation.description());
                methodInfo.put("methodName", method.getName());
                methodInfo.put("parameters", Arrays.toString(method.getParameterTypes()));
                methods.add(methodInfo);
            }
        }
        
        toolInfo.put("methods", methods);
        toolInfo.put("methodCount", methods.size());
        
        return toolInfo;
    }
} 