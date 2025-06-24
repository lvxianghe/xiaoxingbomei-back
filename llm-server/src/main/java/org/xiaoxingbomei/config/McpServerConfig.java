package org.xiaoxingbomei.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.xiaoxingbomei.config.tools.WeatherTool;

@Slf4j
@Configuration
public class McpServerConfig {

    @Autowired
    private WeatherTool weatherTool;

    @PostConstruct
    public void init() {
        log.info("=================================");
        log.info("MCP Server 配置初始化完成");
        log.info("WeatherTool 已注册: {}", weatherTool != null);
        log.info("Spring AI MCP Server应该自动发现@Component + @Tool");
        
        // 验证WeatherTool方法
        if (weatherTool != null) {
            try {
                String testResult = weatherTool.getWeather("测试城市");
                log.info("✅ WeatherTool测试调用成功: {}", testResult);
            } catch (Exception e) {
                log.error("❌ WeatherTool测试调用失败", e);
            }
        }
        
        log.info("如果仍然无法发现工具，请检查MCP Server配置和依赖版本");
        log.info("=================================");
    }
} 