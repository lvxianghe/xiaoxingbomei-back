package org.xiaoxingbomei.config.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库工具 - 用于演示MCP工具扩展
 */
@Slf4j
@Service
@Component
public class DatabaseTool {

    // 模拟数据库数据
    private final Map<String, Object> mockDatabase = new HashMap<>();

    public DatabaseTool()
    {
        // 初始化模拟数据
        mockDatabase.put("user_001", Map.of("name", "张三", "age", 25, "city", "北京"));
        mockDatabase.put("user_002", Map.of("name", "李四", "age", 30, "city", "上海"));
        mockDatabase.put("user_003", Map.of("name", "王五", "age", 28, "city", "广州"));
        log.info("🗄️ DatabaseTool 初始化完成，模拟数据库包含 {} 条记录", mockDatabase.size());
    }

    @Tool(name = "queryUser", description = "根据用户ID查询用户信息")
    public String queryUser(String userId) {
        log.info("[DatabaseTool] 🔍 查询用户信息 - userId: {}", userId);
        
        Object userData = mockDatabase.get(userId);
        if (userData == null) {
            String result = "用户ID " + userId + " 不存在";
            log.info("[DatabaseTool] ❌ 查询结果: {}", result);
            return result;
        }
        
        String result = "用户信息: " + userData.toString();
        log.info("[DatabaseTool] ✅ 查询结果: {}", result);
        return result;
    }

    @Tool(name = "getUserCount", description = "获取数据库中的用户总数")
    public String getUserCount() {
        log.info("[DatabaseTool] 📊 获取用户总数");
        
        int count = mockDatabase.size();
        String result = "数据库中共有 " + count + " 个用户";
        log.info("[DatabaseTool] ✅ 统计结果: {}", result);
        return result;
    }
} 