package org.xiaoxingbomei.config.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * VectorStore配置类
 * 使用Spring AI原生配置的OpenAI EmbeddingModel
 */
@Slf4j
@Configuration
public class VectorStoreConfig
{

    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
        log.info("✅ [VectorStore] 使用Spring AI自动装配的OpenAI EmbeddingModel创建VectorStore");
        return SimpleVectorStore.builder(embeddingModel).build();
    }
} 