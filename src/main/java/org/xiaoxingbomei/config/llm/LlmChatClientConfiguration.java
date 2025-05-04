package org.xiaoxingbomei.config.llm;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * 大模型应用对话的配置
 */
@Configuration
public class LlmChatClientConfiguration
{

//    /**
//     * 配置ollama的chatClient
//     */
//    @Bean
//    public ChatClient ollamaChatClient(OllamaChatModel model, ChatMemory chatMemory)
//    {
//        return ChatClient
//                .builder(model)
//                .defaultSystem(SystemPromptConstant.XIAOXINGBOMEI_SYSTEM_PROMPT)
//                .defaultAdvisors
//                        (
//                                new SimpleLoggerAdvisor(),               // 日志增强
//                                new MessageChatMemoryAdvisor(chatMemory) // 会话记忆增强
//                        )
//                .build();
//    }
//
//    /**
//     * 配置openai的chatClient
//     */
//    @Bean
//    public ChatClient openAiChatClient(OpenAiChatModel model,ChatMemory chatMemory)
//    {
//
//        return ChatClient
//                .builder(model)
//                .defaultSystem(SystemPromptConstant.GAME_SYSTEM_PROMPT)
//                .defaultAdvisors
//                        (
//                                new SimpleLoggerAdvisor(),               // 日志增强
//                                new MessageChatMemoryAdvisor(chatMemory) // 会话记忆增强
//                        )
//                .build();
//    }
//
//    /**
//     * 配置智能客服的chatClient
//     */
//    @Bean
//    public ChatClient serviceChatClient(OpenAiChatModel model, ChatMemory chatMemory, ProgrammerTools programmerTools)
//    {
//        return ChatClient
//                .builder(model)
//                .defaultSystem(SystemPromptConstant.SERVICE_SYSTEM_PROMPT)
//                .defaultAdvisors(
//                        new SimpleLoggerAdvisor(),
//                        new MessageChatMemoryAdvisor(chatMemory)
//                )
//                .defaultTools(programmerTools)
//                .build();
//    }
//
//    @Bean("openAiGrokClient")
//    public ChatClient openAiGrokClient(ChatMemory chatMemory)
//    {
//        // 构建 OpenAiApi
//        OpenAiApi api = new OpenAiApi("https://chatapi.littlewheat.com", "sk-YiB76FYEBysWcTUw69FEGVfwqMzf1iKMILvobZg3ALVHFl63"); // 用配置文件或环境变量替代
//
//        // 构建 OpenAiChatOptions
//        OpenAiChatOptions options = OpenAiChatOptions.builder()
//                .model("grok-3")
//                .temperature(0.7)
//                .build();
//
//        // 创建 OpenAiChatModel
//        OpenAiChatModel model = OpenAiChatModel.builder()
//                .openAiApi(api)
//                .defaultOptions(options)
//                .build();
//
//        // 构建 ChatClient
//        return ChatClient.builder(model)
//                .defaultSystem(SystemPromptConstant.GAME_SYSTEM_PROMPT)
//                .defaultAdvisors(
//                        new SimpleLoggerAdvisor(),
//                        new MessageChatMemoryAdvisor(chatMemory)
//                )
//                .build();
//    }


}
