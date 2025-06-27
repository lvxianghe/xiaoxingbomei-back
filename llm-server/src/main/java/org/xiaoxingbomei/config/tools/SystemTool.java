package org.xiaoxingbomei.config.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 系统工具 - 用于演示MCP工具扩展
 */
@Slf4j
@Service
@Component
public class SystemTool {

    private final Random random = new Random();

    public SystemTool() {
        log.info("🔧 SystemTool 初始化完成");
    }

    @Tool(name = "getCurrentTime", description = "获取当前系统时间")
    public String getCurrentTime() {
        log.info("[SystemTool] ⏰ 获取当前时间");
        
        LocalDateTime now = LocalDateTime.now();
        String result = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("[SystemTool] ✅ 当前时间: {}", result);
        return "当前时间: " + result;
    }

    @Tool(name = "generateRandomNumber", description = "生成指定范围内的随机数")
    public String generateRandomNumber(int min, int max) {
        log.info("[SystemTool] 🎲 生成随机数 - 范围: {} 到 {}", min, max);
        
        if (min >= max) {
            String result = "错误: 最小值必须小于最大值";
            log.warn("[SystemTool] ❌ 参数错误: {}", result);
            return result;
        }
        
        int randomNum = random.nextInt(max - min + 1) + min;
        String result = "生成的随机数: " + randomNum;
        log.info("[SystemTool] ✅ 随机数生成结果: {}", result);
        return result;
    }

    @Tool(name = "getSystemInfo", description = "获取系统基本信息")
    public String getSystemInfo() {
        log.info("[SystemTool] 💻 获取系统信息");
        
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024; // MB
        long freeMemory = runtime.freeMemory() / 1024 / 1024; // MB
        long usedMemory = totalMemory - freeMemory;
        
        String result = String.format("系统信息:\n- Java版本: %s\n- 总内存: %d MB\n- 已用内存: %d MB\n- 可用内存: %d MB\n- 处理器核心数: %d",
                System.getProperty("java.version"),
                totalMemory,
                usedMemory,
                freeMemory,
                runtime.availableProcessors());
        
        log.info("[SystemTool] ✅ 系统信息获取完成");
        return result;
    }
} 