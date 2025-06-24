package org.xiaoxingbomei;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Client_MCP {
    
    public static void main(String[] args) {
        SpringApplication.run(Client_MCP.class, args);
        log.info("MCP Client 启动成功！");
    }
} 