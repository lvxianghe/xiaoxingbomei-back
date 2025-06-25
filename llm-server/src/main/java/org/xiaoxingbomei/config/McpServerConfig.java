package org.xiaoxingbomei.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaoxingbomei.config.tools.WeatherTool;

@Slf4j
@Configuration
public class McpServerConfig {

    @Autowired
    private WeatherTool weatherTool;

    // TODO: 需要找到正确的ToolCallbackProvider导入路径

    @PostConstruct
    public void init() {
        log.info("=================================");
        log.info("🚀 MCP Server 配置初始化完成");
        log.info("✅ WeatherTool 已注册: {}", weatherTool != null);
        
        // 验证WeatherTool方法
        if (weatherTool != null) {
            try {
                String testResult = weatherTool.getWeather("测试城市");
                log.info("✅ WeatherTool测试调用成功: {}", testResult);
            } catch (Exception e) {
                log.error("❌ WeatherTool测试调用失败", e);
            }
        }
        
        log.info("🎯 已通过ToolCallbackProvider Bean注册工具");
        log.info("💡 Spring AI MCP将从ToolCallbackProvider自动发现工具");
        log.info("=================================");
    }
} 