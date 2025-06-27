package org.xiaoxingbomei;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.xiaoxingbomei.config.McpClientManager;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Client_MCP {
    
    @Autowired
    private McpClientManager mcpClientManager;
    
    public static void main(String[] args)
    {
        SpringApplication.run(Client_MCP.class, args);
        log.info("MCP Client 启动成功！");
    }


    @Bean
    public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools,
                                                 ConfigurableApplicationContext context)
    {

        return args -> {
            log.info("🚀 MCP Client 启动完成！");
            log.info("🌐 MCP Client管理端点: http://localhost:28929/mcp");
            
            // 等待一下让McpClientManager完成初始化
            Thread.sleep(1000);

            // 检查是否有可用工具
            int availableTools = mcpClientManager.getAllTools().size();
            if (availableTools == 0) {
                log.warn("⚠️ 当前没有可用的工具，跳过测试");
                log.info("💡 请启动MCP Server后重试");
            } else {
                log.info("🔧 发现 {} 个可用工具，开始测试...", availableTools);
                
                var chatClient = chatClientBuilder
                        .defaultToolCallbacks(tools)
                        .build();

                // 简单测试一个工具调用
                testSingleTool(chatClient, "今天杭州的天气如何？");
            }

            log.info("🌟 MCP Client 服务已启动，可以通过以下接口访问:");
            log.info("   - 健康检查: GET http://localhost:28929/mcp/health");
            log.info("   - 工具列表: GET http://localhost:28929/mcp/tools");
            log.info("   - 服务器状态: GET http://localhost:28929/mcp/servers");
            log.info("   - 工具刷新: POST http://localhost:28929/mcp/tools/refresh");
            log.info("🚀 服务将持续运行，按 Ctrl+C 停止...");
            
            // 移除 context.close() 让服务持续运行
        };
    }
    
    /**
     * 测试单个工具
     */
    private void testSingleTool(ChatClient chatClient, String question) {
        try {
            log.info("\n🔍 测试工具调用 - 问题: {}", question);
            String response = chatClient.prompt(question).call().content();
            log.info("🤖 AI回答: {}", response);
        } catch (Exception e) {
            log.error("❌ 工具调用测试失败", e);
        }
    }

} 