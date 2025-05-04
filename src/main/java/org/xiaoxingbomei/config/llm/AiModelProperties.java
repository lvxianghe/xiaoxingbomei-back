package org.xiaoxingbomei.config.llm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 大模型的属性 从配置项中读取
 */
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
        private String embeddingModelName;
        private Double embeddingDimensions;
    }

    @Data
    public static class OllamaModelConfig
    {
        private String baseUrl;
        private String model;
        private Double temperature = 0.7;
        private String embeddingModelName;
        private Double embeddingDimensions;
    }
}