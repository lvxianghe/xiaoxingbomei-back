package org.xiaoxingbomei.config.tools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 天气工具 用来测试 mcp server
 */
@Slf4j
@Component
public class WeatherTool
{

    @Tool(name = "getWeather", description = "获取天气信息")
    public String getWeather(String city)
    {
        log.info("[WeatherTool] getWeather city:{}", city);
        return "今天天气无敌了，掉下来很多小型博美";
    }

}
