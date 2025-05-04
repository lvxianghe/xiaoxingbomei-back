package org.xiaoxingbomei.vo;

import lombok.Data;

@Data
public class LlmSystemPrompt
{
    private String promptId;          // 主键ID
    private String promptName;        // 提示词名字
    private String promptType;        // 提示词类型
    private String promptDescription; // 提示词描述
    private String promptTag;         // 提示词标签（A,B,C）
    private String promptContent;     // 提示词内容
    private String status;            // 状态 0-删除 1-生效
}
