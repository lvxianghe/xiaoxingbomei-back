package org.xiaoxingbomei.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.service.AiService;
import org.xiaoxingbomei.utils.Request_Utils;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@Service
@Slf4j
public class AiServiceImpl implements AiService
{


//    private final ChatClient chatClient;
//
//    public AiServiceImpl(ChatClient chatClient) {
//        this.chatClient = chatClient;
//    }

//    @Autowired
//    @Qualifier("openAiChatClient")
//    private ChatClient openAiChatClient;

    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaChatClient;


    // ===================================================================

    @Override
    public ResponseEntity chat_for_string(String paramString)
    {
        log.info("chat_for_string");
        // 1、获取前端参数
        String prompt = Request_Utils.getParam(paramString, "prompt");

        // 2、普通模式
        String resultContent = ollamaChatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        //  3、封装返回体
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultContent", resultContent);
        resultMap.put("prompt", prompt);
        return ResponseEntity.success(resultMap);
    }
    @Override
    public Flux<String> chat_for_stream(String prompt)
    {
        log.info("chat_for_stream with prompt: {}", prompt);
        // 使用OpenAI进行流式响应
        return ollamaChatClient
                .prompt()
                .user(prompt)
                .stream()
                .content();
    }

}
