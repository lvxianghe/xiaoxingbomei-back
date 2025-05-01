package org.xiaoxingbomei.config.llm;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaoxingbomei.constant.SystemPromptConstant;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableConfigurationProperties(AiModelProperties.class)
public class AiChatClientAutoConfiguration
{



    @Bean("openaiChatClientMap")
    public Map<String, ChatClient> openAiChatClientMap(AiModelProperties properties,
                                                       ChatMemory chatMemory) {
        Map<String, ChatClient> map = new HashMap<>();
        properties.getOpenai().forEach((name, config) -> {
            OpenAiApi api = new OpenAiApi(config.getBaseUrl(), config.getApiKey());
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(config.getModel())
                    .temperature(config.getTemperature())
                    .build();
            OpenAiChatModel model = OpenAiChatModel.builder()
                    .openAiApi(api)
                    .defaultOptions(options)
                    .build();
            map.put(name, ChatClient.builder(model)
                    .defaultSystem(SystemPromptConstant.GAME_SYSTEM_PROMPT)
                    .defaultAdvisors(new SimpleLoggerAdvisor(), new MessageChatMemoryAdvisor(chatMemory))
                    .build());
        });
        return map;
    }


    @Bean("ollamaChatClientMap")
    public Map<String, ChatClient> ollamaChatClientMap(AiModelProperties properties,
                                                       ChatMemory chatMemory)
    {
        Map<String, ChatClient> map = new HashMap<>();
        properties.getOllama().forEach((name, config) -> {
            OllamaApi api = new OllamaApi(config.getBaseUrl());
            OllamaOptions options = OllamaOptions.builder()
                    .model(config.getModel())
                    .temperature(config.getTemperature())
                    .build();
            OllamaChatModel model = OllamaChatModel.builder()
                    .ollamaApi(api)
                    .defaultOptions(options)
                    .build();
            map.put(name, ChatClient.builder(model)
                    .defaultSystem(SystemPromptConstant.GAME_SYSTEM_PROMPT)
                    .defaultAdvisors(new SimpleLoggerAdvisor(), new MessageChatMemoryAdvisor(chatMemory))
                    .build());
        });
        return map;
    }


}
