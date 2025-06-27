package org.xiaoxingbomei.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * MCP Client配置类
 */
@Slf4j
@Configuration
public class McpClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        log.info("🔧 创建RestTemplate用于MCP Server通信");
        return new RestTemplate();
    }
} 