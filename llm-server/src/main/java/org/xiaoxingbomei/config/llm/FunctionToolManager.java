package org.xiaoxingbomei.config.llm;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaoxingbomei.config.tools.CoffeeTools;

import java.util.HashMap;
import java.util.Map;

/**
 * 函数工具管理器
 * 负责注册和管理所有可用的AI工具
 */
@Slf4j
@Component
public class FunctionToolManager
{
    
    @Autowired
    private CoffeeTools coffeeTools;
    
    // 工具注册表：toolId -> 工具实例
    private final Map<String, Object> toolRegistry = new HashMap<>();
    
    /**
     * 初始化工具注册表
     */
    @PostConstruct
    public void initTools()
    {
        // 注册咖啡客服工具
        toolRegistry.put("coffee_tools", coffeeTools);
        
        // 未来可以在这里注册更多工具
        // toolRegistry.put("ecommerce_tools", ecommerceTools);
        // toolRegistry.put("bank_service_tools", bankServiceTools);
        
        log.info("函数工具管理器初始化完成，已注册 {} 个工具类", toolRegistry.size());
        toolRegistry.keySet().forEach(toolId -> 
            log.info("已注册工具: {} -> {}", toolId, toolRegistry.get(toolId).getClass().getSimpleName())
        );
    }
    
    /**
     * 根据工具ID获取工具实例
     * @param toolId 工具ID
     * @return 工具实例，如果找不到返回null
     */
    public Object getToolById(String toolId)
    {
        if (toolId == null || toolId.trim().isEmpty())
        {
            return null;
        }
        
        Object tool = toolRegistry.get(toolId);
        if (tool == null)
        {
            log.warn("未找到工具ID: {}", toolId);
        } else
        {
            log.debug("获取到工具: {} -> {}", toolId, tool.getClass().getSimpleName());
        }
        
        return tool;
    }
    
    /**
     * 检查工具是否存在
     * @param toolId 工具ID
     * @return 是否存在
     */
    public boolean hasTools(String toolId) {
        return toolId != null && toolRegistry.containsKey(toolId);
    }
    
    /**
     * 获取所有可用的工具ID
     * @return 工具ID集合
     */
    public Map<String, String> getAllToolIds()
    {
        Map<String, String> result = new HashMap<>();
        toolRegistry.forEach((toolId, toolInstance) -> 
            result.put(toolId, toolInstance.getClass().getSimpleName())
        );
        return result;
    }
} 