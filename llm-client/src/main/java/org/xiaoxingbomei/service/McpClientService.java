package org.xiaoxingbomei.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class McpClientService {

    @Autowired(required = false)
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    /**
     * 官方推荐的MCP工具调用方式
     * 直接使用ToolCallback进行工具调用
     */
    public String callWeatherTool(String city) {
        log.info("🌤️ 使用官方MCP方式调用天气工具, 城市: {}", city);
        
        try {
            // 检查MCP工具是否可用
            if (toolCallbackProvider == null) {
                return "❌ MCP工具提供者未配置";
            }
            
            ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();
            if (tools.length == 0) {
                return "❌ 没有发现可用的MCP工具";
            }
            
            log.info("✅ 发现 {} 个MCP工具", tools.length);
            
            // 直接使用第一个可用的工具（通常是天气工具）
            ToolCallback weatherTool = tools[0];
            String arguments = String.format("{\"city\": \"%s\"}", city);
            
            log.info("📝 调用MCP工具: {}", arguments);
            String result = weatherTool.call(arguments);
            log.info("✅ MCP工具调用成功");
            
            return result;
            
        } catch (Exception e) {
            log.error("❌ MCP工具调用失败", e);
            return "MCP工具调用失败: " + e.getMessage();
        }
    }

    /**
     * 获取可用的MCP工具列表
     */
    public List<Map<String, Object>> getAvailableTools() {
        try {
            if (toolCallbackProvider != null) {
                ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
                
                List<Map<String, Object>> toolDetails = new ArrayList<>();
                for (int i = 0; i < toolCallbacks.length; i++) {
                    ToolCallback callback = toolCallbacks[i];
                    Map<String, Object> toolInfo = new HashMap<>();
                    
                    toolInfo.put("index", i);
                    toolInfo.put("name", callback.getToolDefinition() != null ? 
                        "MCP工具" : "未知");
                    toolInfo.put("description", callback.getToolDefinition() != null ? 
                        "MCP工具描述" : "无描述");
                    toolInfo.put("class", callback.getClass().getSimpleName());
                    toolInfo.put("status", "available");
                    
                    toolDetails.add(toolInfo);
                }
                
                return toolDetails;
            } else {
                return List.of(Map.of("status", "MCP Tool Callback Provider未连接"));
            }
        } catch (Exception e) {
            log.error("获取工具列表失败", e);
            return List.of(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 测试MCP连接状态
     */
    public boolean testConnection() {
        try {
            return toolCallbackProvider != null && 
                   toolCallbackProvider.getToolCallbacks().length > 0;
        } catch (Exception e) {
            log.error("测试连接失败", e);
            return false;
        }
    }
    
    /**
     * 获取MCP工具数量
     */
    public int getToolCallbackCount() {
        try {
            if (toolCallbackProvider != null) {
                return toolCallbackProvider.getToolCallbacks().length;
            }
            return 0;
        } catch (Exception e) {
            log.error("获取工具数量失败", e);
            return -1;
        }
    }
    
    /**
     * 分析MCP Provider详细信息
     */
    public Map<String, Object> analyzeProvider() {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            if (toolCallbackProvider != null) {
                analysis.put("provider_class", toolCallbackProvider.getClass().getName());
                analysis.put("provider_available", true);
                
                ToolCallback[] callbacks = toolCallbackProvider.getToolCallbacks();
                analysis.put("callbacks_array_length", callbacks.length);
                analysis.put("callbacks_is_null", callbacks == null);
                
                if (callbacks.length > 0) {
                    List<Map<String, Object>> callbackDetails = new ArrayList<>();
                    for (int i = 0; i < callbacks.length; i++) {
                        Map<String, Object> callbackInfo = new HashMap<>();
                        ToolCallback callback = callbacks[i];
                        
                        callbackInfo.put("index", i);
                        callbackInfo.put("class", callback.getClass().getName());
                        
                        try {
                            if (callback.getToolDefinition() != null) {
                                callbackInfo.put("name", "MCP工具");
                                callbackInfo.put("description", "MCP工具描述");
                                callbackInfo.put("has_definition", true);
                            } else {
                                callbackInfo.put("has_definition", false);
                            }
                        } catch (Exception defE) {
                            callbackInfo.put("definition_error", defE.getMessage());
                        }
                        
                        callbackDetails.add(callbackInfo);
                    }
                    analysis.put("callback_details", callbackDetails);
                } else {
                    analysis.put("no_callbacks_reason", "toolCallbackProvider.getToolCallbacks()返回空数组");
                }
                
            } else {
                analysis.put("provider_available", false);
                analysis.put("provider_null_reason", "Spring未注入SyncMcpToolCallbackProvider");
            }
        } catch (Exception e) {
            analysis.put("analysis_error", e.getMessage());
            log.error("分析Provider失败", e);
        }
        
        return analysis;
    }

    /**
     * 检查是否有ToolCallbackProvider
     */
    public boolean hasToolCallbackProvider() {
        return toolCallbackProvider != null;
    }
} 