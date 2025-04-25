package org.xiaoxingbomei.config.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaChatClientConfiguration
{

    /**
     * 配置会话记忆
     */
    @Bean
    public ChatMemory chatMemory()
    {
        return new InMemoryChatMemory();
    }

    /**
     * 配置ollama的chatClient
     */
    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel model,ChatMemory chatMemory)
    {
        return ChatClient
                .builder(model)
                .defaultSystem("你是一个热心的智能助手，你的名字是小型博美，请以小型博美的身份回答问题")
                .defaultAdvisors
                        (
                                new SimpleLoggerAdvisor(),               // 日志增强
                                new MessageChatMemoryAdvisor(chatMemory) // 会话记忆增强
                        )
                .build();
    }


}
