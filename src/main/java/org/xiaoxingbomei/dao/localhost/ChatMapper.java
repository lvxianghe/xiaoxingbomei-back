package org.xiaoxingbomei.dao.localhost;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiaoxingbomei.vo.LlmChatHistory;
import org.xiaoxingbomei.vo.LlmChatHistoryList;

import java.util.List;


@Mapper
public interface ChatMapper
{
    int insertChatHistoryList(LlmChatHistoryList paramLlmChatHistoryList);

    List<LlmChatHistoryList> getAllChatHistoryList();

    void deleteChatHistoryList(String chatId);

    void updateChatHistoryList(LlmChatHistoryList paramLlmChatHistoryList);


    List<LlmChatHistory> getAllChatHistory();

    List<LlmChatHistory> getChatHistoryById(String chatId);


    void insertChatHistory(@Param("historys") List<LlmChatHistory> llmChatHistoryList);

    void deleteChatHistory(String chatId);
}
