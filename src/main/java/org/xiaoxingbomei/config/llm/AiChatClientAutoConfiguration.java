package org.xiaoxingbomei.config.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaoxingbomei.tools.ProgrammerTools;

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
    private ProgrammerTools programmerTools;

    /**
     * 配置会话记忆
     */
    @Bean
    public ChatMemory chatMemory()
    {
        return new InMemoryChatMemory();
    }

    @Bean("openaiChatClientMap")
    public Map<String, ChatClient> openAiChatClientMap(AiModelProperties properties,
                                                       ChatMemory chatMemory)
    {
        Map<String, ChatClient> map = new HashMap<>();
        properties.getOpenai().forEach((name, config) ->
        {
            OpenAiApi api = new OpenAiApi(config.getBaseUrl(), config.getApiKey());
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(config.getModel())
                    .temperature(config.getTemperature())
                    .build();
            OpenAiChatModel model = OpenAiChatModel.builder()
                    .openAiApi(api)
                    .defaultOptions(options)
                    .build();
            
            log.info("创建OpenAI ChatClient: {}, 添加ProgrammerTools支持", name);
            map.put(name, ChatClient.builder(model)
                    .defaultAdvisors(new SimpleLoggerAdvisor(), new MessageChatMemoryAdvisor(chatMemory))
//                    .defaultTools(programmerTools)
                    .build());
        });
        return map;
    }


    @Bean("ollamaChatClientMap")
    public Map<String, ChatClient> ollamaChatClientMap(AiModelProperties properties,
                                                       ChatMemory chatMemory)
    {
        Map<String, ChatClient> map = new HashMap<>();
        properties.getOllama().forEach((name, config) ->
        {
            OllamaApi api = new OllamaApi(config.getBaseUrl());
            OllamaOptions options = OllamaOptions.builder()
                    .model(config.getModel())
                    .temperature(config.getTemperature())
                    .build();
            OllamaChatModel model = OllamaChatModel.builder()
                    .ollamaApi(api)
                    .defaultOptions(options)
                    .build();
            
            log.info("创建Ollama ChatClient: {}, 添加ProgrammerTools支持", name);
            map.put(name, ChatClient.builder(model)
                    .defaultAdvisors(new SimpleLoggerAdvisor(), new MessageChatMemoryAdvisor(chatMemory))
//                    .defaultTools(programmerTools)
                    .build());
        });
        return map;
    }

    // ===================================下面是特殊的单独配置

    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel)
    {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel model, ChatMemory chatMemory, VectorStore vectorStore) {
        return ChatClient
                .builder(model)
                .defaultSystem("请根据上下文回答问题，遇到上下文没有的问题，不要随意编造。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(
                                vectorStore,
                                SearchRequest.builder()
                                        .similarityThreshold(0.6)
                                        .topK(2)
                                        .build()
                        )
                )
                .build();
    }


}
