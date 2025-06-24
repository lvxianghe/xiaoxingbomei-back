package org.xiaoxingbomei.config.llm;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ChatClientFactory
{

    @Resource(name = "openaiChatClientMap")
    private Map<String, ChatClient> openaiClients;

    @Resource(name = "ollamaChatClientMap")
    private Map<String, ChatClient> ollamaClients;

    public ChatClient getClient(String provider, String name)
    {
        return switch (provider.toLowerCase())
        {
            case "openai" -> openaiClients.get(name);
            case "ollama" -> ollamaClients.get(name);
            default -> throw new IllegalArgumentException("不支持的 provider: " + provider);
        };
    }

    @PostConstruct
    public void logClientInfo()
    {
        log.info("==============================================");
        log.info("               AiChatClient 配置              ");
        log.info("==============================================");
        log.info(String.format("| %-12.5s | %-17s |", "模型类型", "模型名称"));
        log.info("|-----------------|----------------------|");

        // 打印 OpenAI 模型信息
        if (!openaiClients.isEmpty()) {
            openaiClients.forEach((name, client) ->
                    log.info(String.format("| %-15s | %-20s |", "OpenAI", name))
            );
        } else {
            log.warn("| %-15s | %-20s |", "OpenAI", "没有加载任何模型");
        }

        // 打印 Ollama 模型信息
        if (!ollamaClients.isEmpty()) {
            ollamaClients.forEach((name, client) ->
                    log.info(String.format("| %-15s | %-20s |", "Ollama", name))
            );
        } else {
            log.warn("| %-15s | %-20s |", "Ollama", "没有加载任何模型");
        }

        log.info("==============================================");
    }


}
