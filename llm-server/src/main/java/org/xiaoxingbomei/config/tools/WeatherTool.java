package org.xiaoxingbomei.config.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 天气工具 用来测试 mcp server
 * 使用@Service确保能被Spring AI的MCP框架发现
 */
@Slf4j
@Service  // 改用@Service，确保被MCP框架扫描到
@Component
public class WeatherTool
{

    @Tool(name = "getWeather", description = "获取某个城市的天气信息")
    public String getWeather(String city) {
        log.info("[WeatherTool] ✅ MCP工具调用 - getWeather city: {}", city);
        
        String result = new String((city).getBytes(), StandardCharsets.UTF_8) + "今天天气无敌了，掉下来很多小型博美";
        log.info("[WeatherTool] 🌤️ 返回结果: {}", result);
        
        return result;
    }
}
