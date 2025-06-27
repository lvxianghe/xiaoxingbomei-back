package org.xiaoxingbomei.config.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.xiaoxingbomei.config.tools.CoffeeTools;
import org.xiaoxingbomei.config.tools.DatabaseTool;
import org.xiaoxingbomei.config.tools.SystemTool;
import org.xiaoxingbomei.config.tools.WeatherTool;

@Slf4j
@Configuration
public class McpToolConfig {
    
    @Autowired
    private WeatherTool weatherTool;
    
    @Autowired
    private CoffeeTools coffeeTools;
    
    @Autowired
    private DatabaseTool databaseTool;
    
    @Autowired
    private SystemTool systemTool;

    @Bean
    @Primary
    public ToolCallbackProvider mcpServerTools() {
        log.info("🔧 MCP Server 提供工具:");
        log.info("   ├─ WeatherTool: 天气查询工具");
        log.info("   ├─ CoffeeTools: 咖啡订购工具");
        log.info("   ├─ DatabaseTool: 数据库工具");
        log.info("   └─ SystemTool: 系统工具");
        
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherTool, coffeeTools, databaseTool, systemTool)
                .build();
    }
}
