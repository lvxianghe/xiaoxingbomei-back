package org.xiaoxingbomei.config.llm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ai")
@Data
public class AiModelProperties {

    private Map<String, OpenAiModelConfig> openai = new HashMap<>();
    private Map<String, OllamaModelConfig> ollama = new HashMap<>();

    @Data
    public static class OpenAiModelConfig
    {
        private String baseUrl;
        private String apiKey;
        private String model;
        private Double temperature = 0.7;
    }

    @Data
    public static class OllamaModelConfig
    {
        private String baseUrl;
        private String model;
        private Double temperature = 0.7;
    }
}