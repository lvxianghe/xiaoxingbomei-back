package org.xiaoxingbomei.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class McpClientService {

    @Autowired(required = false)
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    /**
     * 获取可用的工具列表
     */
    public List<Map<String, Object>> getAvailableTools() {
        try {
            if (toolCallbackProvider != null) {
                ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
                log.info("MCP工具回调提供者找到，工具数量: {}", toolCallbacks.length);
                
                // 简化工具信息返回
                return List.of(Map.of(
                    "count", toolCallbacks.length,
                    "status", "MCP工具回调提供者已连接"
                ));
            }
            log.warn("MCP Tool Callback Provider未找到");
            return List.of(Map.of("status", "MCP Tool Callback Provider未找到"));
        } catch (Exception e) {
            log.error("获取工具列表失败", e);
            return List.of(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 调用天气工具
     */
    public String getWeather(String city) {
        try {
            if (toolCallbackProvider != null) {
                ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
                
                log.info("可用工具数量: {}", toolCallbacks.length);
                
                // 尝试调用第一个可用的工具
                if (toolCallbacks.length > 0) {
                    try {
                        ToolCallback callback = toolCallbacks[0];
                        // 创建ToolContext - 使用静态方法
                        ToolContext toolContext =new ToolContext(Map.of("city", city));
                        // 调用工具
                        String result = callback.call("getWeather", toolContext);
                        log.info("工具调用结果: {}", result);
                        return result;
                    } catch (Exception callException) {
                        log.error("调用工具时出错", callException);
                        return "工具调用出错: " + callException.getMessage();
                    }
                }
                return "未找到可用工具";
            }
            return "MCP Tool Callback Provider未连接";
        } catch (Exception e) {
            log.error("调用天气工具失败", e);
            return "调用失败: " + e.getMessage();
        }
    }

    /**
     * 测试连接
     */
    public boolean testConnection() {
        try {
            boolean hasProvider = toolCallbackProvider != null;
            log.info("MCP连接测试 - Provider存在: {}", hasProvider);
            
            if (hasProvider) {
                ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
                boolean hasTools = toolCallbacks != null && toolCallbacks.length > 0;
                log.info("MCP连接测试 - 工具数量: {}", toolCallbacks != null ? toolCallbacks.length : 0);
                return hasTools;
            }
            return false;
        } catch (Exception e) {
            log.error("测试连接失败", e);
            return false;
        }
    }
} 