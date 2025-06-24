package org.xiaoxingbomei.service;


import org.springframework.web.multipart.MultipartFile;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;
import org.xiaoxingbomei.vo.LlmChatHistory;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService
{
    Flux<String>   chat
            (
                    String              prompt,          // 用户提示词
                    String              chatId,          // 会话id
                    String              isStream,        // 是否流式打印
                    String              modelProvider,   // 模型提供商
                    String              modelName,       // 模型名称
                    String              systemPromptId,  // 系统提示词ID（可选，为空使用默认，会自动获取对应的工具配置）
                    List<MultipartFile> files            // 多模态文件（可选，为空使用默认，决定路由普通文本对话还是多模态对话）
            );

    GlobalResponse chat_for_string(String paramString);
    Flux<String>   chat_for_stream(String prompt,String chatId);

    GlobalResponse getAllChatHistoryList();
    GlobalResponse insertChatHistoryList(String paramString);
    GlobalResponse deleteChatHistoryList(String paramString);
    GlobalResponse updateChatHistoryList(String paramString);

    List<LlmChatHistory> getChatHistoryById(String chatId);

}
