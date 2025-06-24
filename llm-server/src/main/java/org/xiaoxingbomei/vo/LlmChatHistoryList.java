package org.xiaoxingbomei.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大模型会话历史列表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LlmChatHistoryList
{
    private String chatId;      // 会话id
    private String chatTittle;  // 会话标题
    private String chatTag;     // 会话标签
//    private String chatType;    // 会话类型
    private String createTime;  // 创建时间
    private String updateTime;  // 更新时间
}
