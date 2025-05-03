package org.xiaoxingbomei.vo;

import lombok.Data;

/**
 *
 */
@Data
public class LlmModel
{
    private String modelProvider;     // 模型提供者 如openai/ollama
    private String modelName;         // 模型名称 如 qwen3、deepseek
    private String modelDescription;  // 模型描述
    private String modelTag;          // 模型标签(tagA,tagB,tagC)
}
