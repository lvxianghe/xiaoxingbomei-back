package org.xiaoxingbomei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/simple")
public class SimpleTestController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        log.info("简单ping测试");
        return Map.of(
            "status", "ok",
            "message", "Client服务正常运行",
            "timestamp", System.currentTimeMillis()
        );
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        return Map.of(
            "service", "mcp-client",
            "port", 28929,
            "description", "MCP客户端测试服务"
        );
    }
} 