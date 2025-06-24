package org.xiaoxingbomei.config.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 从配置中自动装配大模型的属性到工厂中去
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(AiModelProperties.class)
public class AiChatClientAutoConfiguration
{

    @Autowired
    ChatMemory chatMemory = MessageWindowChatMemory
            .builder()
            .maxMessages(10)
            .build();

    @Bean("openaiChatClientMap")
    public Map<String, ChatClient> openAiChatClientMap(AiModelProperties properties)
    {
        Map<String, ChatClient> map = new HashMap<>();
        properties.getOpenai().forEach((name, config) ->
        {
            // Spring AI 1.0 版本使用Builder模式构建OpenAiApi
            OpenAiApi api = OpenAiApi.builder()
                    .baseUrl(config.getBaseUrl())
                    .apiKey(config.getApiKey())  // String类型可以直接传入
                    .build();
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(config.getModel())
                    .temperature(config.getTemperature())
                    .build();
            OpenAiChatModel model = OpenAiChatModel.builder()
                    .openAiApi(api)
                    .defaultOptions(options)
                    .build();
            
            log.info("创建OpenAI ChatClient: {}, 配置Advisors链", name);
            map.put(name, ChatClient.builder(model)
                    .defaultAdvisors(
                            // Advisor执行顺序说明（按getOrder()值，越小越优先）：
                            // 1. 日志记录 - 最高优先级，记录完整的请求和响应
                            new SimpleLoggerAdvisor(),
                            
                            // 2. 聊天记忆 - 在处理请求前添加历史对话上下文
                            MessageChatMemoryAdvisor.builder(chatMemory).build()
                            
                            // 📋 可选的高级Advisors（按需启用）：
                            
                            // 3. RAG检索增强 - 如果有向量数据库可启用
                            // QuestionAnswerAdvisor.builder(vectorStore).build(),
                            
                            // 4. 内容安全检查 - 推荐在生产环境启用
                            // SafeGuardAdvisor.builder().build(),
                            
                            // 5. 向量存储记忆 - 用于大规模对话历史检索
                            // VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),
                            
                            // 6. 自定义业务逻辑 - 敏感词过滤、统计记录等
                            // new CustomBusinessAdvisor()
                    )
                    .build());
        });
        return map;
    }

    @Bean("ollamaChatClientMap")
    public Map<String, ChatClient> ollamaChatClientMap(AiModelProperties properties)
    {
        Map<String, ChatClient> map = new HashMap<>();
        properties.getOllama().forEach((name, config) ->
        {
            // Spring AI 1.0 版本构建OllamaApi，使用builder模式
            OllamaApi api = OllamaApi.builder()
                    .baseUrl(config.getBaseUrl())
                    .build();
            OllamaOptions options = OllamaOptions.builder()
                    .model(config.getModel())
                    .temperature(config.getTemperature())
                    .build();
            OllamaChatModel model = OllamaChatModel.builder()
                    .ollamaApi(api)
                    .defaultOptions(options)
                    .build();
            
            log.info("创建Ollama ChatClient: {}, 配置Advisors链", name);
            map.put(name, ChatClient.builder(model)
                    .defaultAdvisors(
                            // Advisor执行顺序说明（按getOrder()值，越小越优先）：
                            // 1. 日志记录 - 最高优先级，记录完整的请求和响应
                            new SimpleLoggerAdvisor(),
                            
                            // 2. 聊天记忆 - 在处理请求前添加历史对话上下文
                            MessageChatMemoryAdvisor.builder(chatMemory).build()
                            
                            // 📋 可选的高级Advisors（同OpenAI配置）：
                            // 3-6. 其他Advisors配置与OpenAI相同，按需启用
                    )
                    .build());
        });
        return map;
    }
}
