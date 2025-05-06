package org.xiaoxingbomei.service.impl;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xiaoxingbomei.constant.SystemPromptConstant;
import org.xiaoxingbomei.dao.localhost.ChatMapper;
import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.factory.ChatClientFactory;
import org.xiaoxingbomei.service.ChatService;
import org.xiaoxingbomei.utils.ChatClient_Utils;
import org.xiaoxingbomei.utils.Request_Utils;
import org.xiaoxingbomei.vo.LlmChatHistory;
import org.xiaoxingbomei.vo.LlmChatHistoryList;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ChatServiceImpl implements ChatService
{

    @Autowired
    private ChatClientFactory chatClientFactory;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    ChatMemory chatMemory;

    @Autowired
    private VectorStore vectorStore;

    // ===================================================================


    @Override
    public Flux<String> chat(String prompt, String chatId, String isStream,String modelProvider,String modelName,String systemPrompt)
    {
        List<Advisor> advisors = List.of
                (
                new MessageChatMemoryAdvisor(chatMemory),
                new SimpleLoggerAdvisor(),
                new QuestionAnswerAdvisor(
                        vectorStore,
                        SearchRequest.builder()
                                .similarityThreshold(0.6)
                                .topK(2)
                                .build()
                )
                );
        ChatClient chatClient = chatClientFactory.getClient(modelProvider, modelName);
        Boolean isStreamBoolean = Boolean.valueOf(isStream);

        // 如果是普通对话，路由是否流式响应
        return isStreamBoolean
                ? (Flux<String>) ChatClient_Utils.chatWithSystemPrompt(chatClient, chatId, prompt, systemPrompt, advisors, true)
                : Flux.just((String) ChatClient_Utils.chatWithSystemPrompt(chatClient, chatId, prompt, systemPrompt, advisors, false));
    }

    @Override
    public ResponseEntity chat_for_string(String prompt)
    {
        log.info("chat_for_string");

        ChatClient chatClient = chatClientFactory.getClient("ollama", "qwen3:14b");
        String resultContent = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultContent", resultContent);
        resultMap.put("prompt", prompt);
        return ResponseEntity.success(resultMap);
    }
    @Override
    public Flux<String> chat_for_stream(String prompt,String chatId)
    {
        log.info("chat_for_stream with prompt: {}", prompt);
        // 使用OpenAI进行流式响应
        ChatClient chatClient = chatClientFactory.getClient("ollama", "qwen3");
        
        // 使用ChatClient_Utils进行调用，它会处理持久化
        return (Flux<String>) ChatClient_Utils.chat(chatClient, chatId, prompt, null, null, true);
    }

    @Override
    public ResponseEntity getAllChatHistoryList()
    {
        // 1、获取前端参数

        // 2、操作
        List<LlmChatHistoryList> allChatHistoryList = chatMapper.getAllChatHistoryList();
        log.info("getAllChatHistoryList:"+allChatHistoryList.get(0).toString());

        // 3、封装响应体
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("allChatHistoryList", allChatHistoryList.toString());
        return ResponseEntity.success(resultMap,"获取全部会话历史成功");
    }

    @Override
    public ResponseEntity insertChatHistoryList(String paramString)
    {
        // 1、获取前端参数
        String chatId       = Request_Utils.getParam(paramString, "chatId");
        String chatTittle   = Request_Utils.getParam(paramString, "chatTittle");
        String chatTag      = Request_Utils.getParam(paramString, "chatTag");
        String chatType     = Request_Utils.getParam(paramString, "chatType");
        String createTime   = Request_Utils.getParam(paramString, "createTime");
        String updateTime   = Request_Utils.getParam(paramString, "updateTime");

        // 2、插入操作
        LlmChatHistoryList llmChatHistoryList = new LlmChatHistoryList();
        llmChatHistoryList.setChatId(chatId);
        llmChatHistoryList.setChatTittle(chatTittle);
        llmChatHistoryList.setChatTag(chatTag);
        llmChatHistoryList.setCreateTime(createTime);
        llmChatHistoryList.setUpdateTime(updateTime);

        chatMapper.insertChatHistoryList(llmChatHistoryList);

        // 3、封装响应体
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("chatId", chatId);
        resultMap.put("chatTittle", chatTittle);
        return ResponseEntity.success(resultMap,"插入新的会话历史成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity deleteChatHistoryList(String paramString)
    {
        // 1、获取前端参数
        String chatId = Request_Utils.getParam(paramString, "chatId");

        chatMapper.deleteChatHistoryList(chatId);
        chatMapper.deleteChatHistory(chatId);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("chatId", chatId);
        return ResponseEntity.success(resultMap,"删除历史会话成功");
    }

    @Override
    public ResponseEntity updateChatHistoryList(String paramString)
    {
        // 1、获取前端参数
        String chatId       = Request_Utils.getParam(paramString, "chatId");
        String chatTittle   = Request_Utils.getParam(paramString, "chatTittle");
        String chatTag      = Request_Utils.getParam(paramString, "chatTag");
        String chatType     = Request_Utils.getParam(paramString, "chatType");
        String updateTime   = Request_Utils.getParam(paramString, "updateTime");
        if (!chatId.isEmpty() && !chatTittle.isEmpty())
        {
            LlmChatHistoryList llmChatHistoryList = new LlmChatHistoryList();
            llmChatHistoryList.setChatId(chatId);
            llmChatHistoryList.setChatTittle(chatTittle);
            llmChatHistoryList.setChatTag(chatTag);
            llmChatHistoryList.setUpdateTime(updateTime);

            chatMapper.updateChatHistoryList(llmChatHistoryList);
        }

        // 3、分装响应体
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("chatId", chatId);
        return ResponseEntity.success(resultMap,"更新历史会话成功");
    }

    @Override
    public List<LlmChatHistory> getChatHistoryById(String chatId)
    {

        // 1、根据chatId获取历史会话

        List<LlmChatHistory> messages = chatMapper.getChatHistoryById(chatId);
        if(messages.isEmpty())
        {
            return List.of();
        }
        return messages;
    }

    /**
     * 将所有的聊天记录初始化到chatMemory中
     */
    @PostConstruct
    private void init()
    {
        System.out.println("OllamaChatClientConfiguration init");
        // 获取全部聊天记录
        List<LlmChatHistory> allChatHistory = chatMapper.getAllChatHistory();
        // 遍历聊天记录，根据chatId分组，将聊天记录转换为Message对象，并添加到chatMemory中
        allChatHistory.stream().collect(
                // 分组，根据chatId分组
                Collectors.groupingBy(LlmChatHistory::getChatId)
        ).forEach(
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

    @Override
    public Flux<String> chat_for_game(String prompt, String chatId)
    {
        ChatClient chatClient = chatClientFactory.getClient("ollama", "qwen3:14b");
        // 使用普通的chat方法，因为ProgrammerTools已经在配置阶段添加
        List<Advisor> advisors = List.of(new SimpleLoggerAdvisor());
        String systemPrompt = SystemPromptConstant.GAME_SYSTEM_PROMPT;
        log.info("chat_for_game: 使用普通chat方法，ProgrammerTools已在配置中添加");
        return (Flux<String>) ChatClient_Utils.chat(chatClient, chatId, prompt, systemPrompt, advisors, true);
    }

    @Override
    public String chat_for_service(String prompt, String chatId)
    {
        ChatClient chatClient = chatClientFactory.getClient("ollama", "qwen3:14b");
        // 使用普通的chat方法，因为ProgrammerTools已经在配置阶段添加
        List<Advisor> advisors = List.of(new SimpleLoggerAdvisor());
        String systemPrompt = SystemPromptConstant.SERVICE_SYSTEM_PROMPT;
        log.info("chat_for_service: 使用普通chat方法，ProgrammerTools已在配置中添加");
        return (String) ChatClient_Utils.chat(chatClient, chatId, prompt, systemPrompt, advisors, false);
    }
}
