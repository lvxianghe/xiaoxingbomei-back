package org.xiaoxingbomei.config.llm;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaoxingbomei.dao.localhost.ChatMapper;
import org.xiaoxingbomei.vo.LlmChatHistory;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChatMemorySyncService
{

    @Autowired
    ChatMemory chatMemory;

    @Autowired
    private ChatMapper chatMapper;

    /**
     * 将所有的聊天记录初始化到chatMemory中
     */
    @PostConstruct
    private void init()
    {

        log.info("ChatMemory init begin");
        // 获取全部聊天记录
        List<LlmChatHistory> allChatHistory = chatMapper.getAllChatHistory();
        // 遍历聊天记录，根据chatId分组，将聊天记录转换为Message对象，并添加到chatMemory中
        allChatHistory.stream().collect
                (
                        // 分组，根据chatId分组
                        Collectors.groupingBy(LlmChatHistory::getChatId)
                ).forEach
                (
                        // 遍历分组
                        (chatId, llmChatHistoryList) ->
                        {
                            // 将聊天记录转换为Message对象
                            List<Message> messages = llmChatHistoryList.stream().map(LlmChatHistory::toMessage).toList();
                            log.info("init chatMemory chatId:{}-->chatHistory:{}", chatId,messages);
                            // 将Message对象添加到chatMemory中
                            chatMemory.add(chatId, messages);
                        }
                );
    }
}
