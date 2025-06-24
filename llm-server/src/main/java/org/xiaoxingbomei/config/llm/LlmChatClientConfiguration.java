package org.xiaoxingbomei.config.llm;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaoxingbomei.constant.SystemPromptConstant;

/**
 * 大模型应用对话的配置
 */
@Configuration
public class LlmChatClientConfiguration
{

    /**
     * 设置OpenAI作为主要的ChatModel，解决Bean冲突问题  SpringAI1.0版本 不需要再显示定义 chatmemory了
     */

    /**
     * 配置ollama的chatClient - 使用defaultSystem设置系统提示词
     */
    @Bean
    public ChatClient ollamaChatClient(@Qualifier("ollamaChatModel") OllamaChatModel model)
    {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemPromptConstant.XIAOXINGBOMEI_SYSTEM_PROMPT) // 设置默认系统提示词
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    /**
     * 配置openai的chatClient - 使用defaultSystem设置系统提示词
     */
    @Bean
    public ChatClient openAiChatClient(@Qualifier("openAiChatModel") OpenAiChatModel model)
    {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemPromptConstant.XIAOXINGBOMEI_SYSTEM_PROMPT) // 设置默认系统提示词
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


}
