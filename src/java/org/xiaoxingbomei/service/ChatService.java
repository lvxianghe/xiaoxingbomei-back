package org.xiaoxingbomei.service;

import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.vo.LlmChatHistory;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService
{
    Flux<String>   chat(String prompt,String chatId,String isStream,String modelProvider,String modelName,String systemPrompt);
    ResponseEntity chat_for_string(String paramString);
    Flux<String>   chat_for_stream(String prompt,String chatId);


    ResponseEntity getAllChatHistoryList();
    ResponseEntity insertChatHistoryList(String paramString);
    ResponseEntity deleteChatHistoryList(String paramString);
    ResponseEntity updateChatHistoryList(String paramString);

    List<LlmChatHistory> getChatHistoryById(String chatId);

    Flux<String> chat_for_game(String prompt, String chatId);

    String chat_for_service(String prompt, String chatId);

}
