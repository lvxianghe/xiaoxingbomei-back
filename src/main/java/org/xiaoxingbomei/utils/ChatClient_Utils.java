package org.xiaoxingbomei.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xiaoxingbomei.dao.localhost.ChatMapper;
import org.xiaoxingbomei.service.FileService;
import org.xiaoxingbomei.tools.ProgrammerTools;
import org.xiaoxingbomei.vo.LlmChatHistory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Slf4j
@Component
public class ChatClient_Utils
{
    // 静态实例，用于在静态方法中访问实例成员
    private static ChatClient_Utils instance;
    
    @Autowired
    private ProgrammerTools programmerTools;
    
    @Autowired
    private ChatMemory chatMemory;
    
    @Autowired
    private ChatMapper chatMapper;
    
    @Autowired
    private FileService fileService;
    
    // 构造函数，在Spring创建Bean时将实例保存到静态变量中
    public ChatClient_Utils() {
        instance = this;
    }

    // 根据标识来决定使用同步还是流式调用
    public static Object chat(ChatClient chatClient,String chatId, String prompt, String systemPrompt, List<Advisor> additionalAdvisors, boolean isStream)
    {
        // 构建 advisors
        List<Advisor> advisors = buildAdvisors(additionalAdvisors);

        // 检查是否有关联文件
        Resource file = null;
        String filename = null;
        if (instance != null && instance.fileService != null)
        {
            try
            {
                file = instance.fileService.getFileByChatId(chatId);
                if (file != null && file.exists())
                {
                    filename = file.getFilename();
                    log.info("找到会话文件: {}，启用RAG增强", filename);
                }
            } catch (Exception e) {
                log.warn("获取会话文件失败: {}", e.getMessage());
            }
        }

        // 执行对话
        Object result;
        if (isStream)
        {
            // 构建prompt builder
            var promptBuilder = chatClient.prompt()
                    .user(prompt)
                    .advisors(advisors)
                    .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId));
            
            // 如果有文件，添加过滤表达式
            if (filename != null) {
                final String finalFilename = filename; // 创建final副本以在lambda中使用
                promptBuilder = promptBuilder.advisors(a -> a.param("FILTER_EXPRESSION", "file_name == '" + finalFilename + "'"));
            }
            
            // 执行流式调用
            result = promptBuilder.stream().content();
        } else
        {
            // 构建prompt builder
            var promptBuilder = chatClient.prompt()
                    .user(prompt)
                    .advisors(advisors)
                    .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId));
            
            // 如果有文件，添加过滤表达式
            if (filename != null) {
                final String finalFilename = filename; // 创建final副本以在lambda中使用
                promptBuilder = promptBuilder.advisors(a -> a.param("FILTER_EXPRESSION", "file_name == '" + finalFilename + "'"));
            }
            
            // 执行同步调用
            result = promptBuilder.call().content();
        }
        
        // 持久化对话历史记录
        try
        {
            persistChatHistory(chatId);
        } catch (Exception e)
        {
            log.error("持久化对话历史记录失败", e);
        }
        
        return result;
    }
    
    /**
     * 持久化对话历史记录
     * 
     * @param chatId 对话ID
     */
    private static void persistChatHistory(String chatId)
    {
        // 验证实例是否已初始化
        if (instance == null || instance.chatMemory == null || instance.chatMapper == null)
        {
            log.error("ChatClient_Utils实例未初始化或必要的依赖未注入，无法持久化对话历史");
            return;
        }
        
        // 获取对话历史记录
        List<Message> messages = instance.chatMemory.get(chatId, Integer.MAX_VALUE);
        if (messages == null || messages.isEmpty())
        {
            log.debug("对话ID {}没有历史记录，跳过持久化", chatId);
            return;
        }
        
        // 转换为LlmChatHistory
        List<LlmChatHistory> llmChatHistorys = new ArrayList<>();
        for (Message message : messages)
        {
            llmChatHistorys.add(new LlmChatHistory(message, chatId));
        }
        
        // 持久化到数据库
        if (!llmChatHistorys.isEmpty())
        {
            log.debug("持久化对话ID {}的{}条历史记录", chatId, llmChatHistorys.size());
            // 先删除再插入
            instance.chatMapper.deleteChatHistory(chatId);
            instance.chatMapper.insertChatHistory(llmChatHistorys);
            log.debug("对话历史记录持久化完成");
        }
    }
    
    /**
     * 使用反射获取ChatClient中的ChatModel，并创建一个带有defaultSystem的新ChatClient
     * 
     * 这个方法使用反射技术尝试从现有的ChatClient中提取底层的ChatModel对象，
     * 然后使用这个模型创建一个新的ChatClient，并设置defaultSystem系统提示词。
     * 
     * 注意: 
     * 1. 这个方法使用Java反射，可能在Spring框架升级后不再适用
     * 2. 反射可能会破坏一些封装，使用时需谨慎
     * 3. 如果反射失败，方法会自动回退到普通的chat方法
     * 4. 此方法会自动添加ProgrammerTools作为默认工具
     * 
     * 使用示例:
     * ```java
     * // 替代普通的chat方法调用
     * ChatClient_Utils.chatWithDefaultSystem(chatClient, chatId, prompt, systemPrompt, advisors, true);
     * ```
     * 
     * @param chatClient 原始的ChatClient对象
     * @param chatId 会话ID
     * @param prompt 用户输入
     * @param systemPrompt 系统提示词，将作为defaultSystem使用
     * @param additionalAdvisors 额外的Advisor列表
     * @param isStream 是否使用流式输出
     * @return 对话结果
     */
    public static Object chatWithSystemPrompt(ChatClient chatClient, String chatId, String prompt, String systemPrompt, List<Advisor> additionalAdvisors, boolean isStream)
    {
        try
        {
            //
            if(StringUtils.isEmpty(systemPrompt))
            {
                systemPrompt = "";
            }

            // 验证静态实例是否已初始化
            if (instance == null || instance.programmerTools == null) {
                log.error("ChatClient_Utils的静态实例未初始化或ProgrammerTools未注入，无法使用defaultTools");
                return chat(chatClient, chatId, prompt, systemPrompt, additionalAdvisors, isStream);
            }
            
            // 获取ChatClient中的model字段
            ChatModel model = extractModelFromChatClient(chatClient);
            
            if (model == null)
            {
                log.error("无法通过反射获取ChatClient中的ChatModel，将使用常规方式调用");
                return chat(chatClient, chatId, prompt, systemPrompt, additionalAdvisors, isStream);
            }
            
            // 获取原始ChatClient的默认advisors
            List<Advisor> originalDefaultAdvisors = extractDefaultAdvisorsFromChatClient(chatClient);
            
            // 创建ChatClient.Builder对象，添加ProgrammerTools
            ChatClient.Builder builder = ChatClient.builder(model)
                    .defaultSystem(systemPrompt);
//                    .defaultTools(instance.programmerTools);
            
            // 如果成功获取到原始默认advisors，则添加到新的ChatClient中
            if (originalDefaultAdvisors != null && !originalDefaultAdvisors.isEmpty())
            {
                builder.defaultAdvisors(originalDefaultAdvisors.toArray(new Advisor[0]));
            } else
            {
                // 如果无法获取原始默认advisors，至少添加一个SimpleLoggerAdvisor
                builder.defaultAdvisors(new SimpleLoggerAdvisor());
            }
            
            // 构建新的ChatClient
            ChatClient clientWithSystemPrompt = builder.build();
            
            // 使用新构建的ChatClient调用chat方法
            return chat(clientWithSystemPrompt, chatId, prompt, systemPrompt, additionalAdvisors, isStream);
            
        } catch (Exception e)
        {
            log.error("使用反射创建defaultSystem ChatClient失败", e);
            // 如果发生异常，回退到常规调用方式
            return chat(chatClient, chatId, prompt, systemPrompt, additionalAdvisors, isStream);
        }
    }
    
    /**
     * 通过反射获取ChatClient中的ChatModel
     * 
     * @param chatClient ChatClient对象
     * @return 提取出的ChatModel，如果失败则返回null
     */
    private static ChatModel extractModelFromChatClient(ChatClient chatClient)
    {
        try
        {
            // 获取ChatClient类
            Class<?> chatClientClass = chatClient.getClass();
            
            // 尝试获取直接的model字段
            try
            {
                Field modelField = chatClientClass.getDeclaredField("model");
                modelField.setAccessible(true);
                return (ChatModel) modelField.get(chatClient);
            } catch (NoSuchFieldException e)
            {
                // 字段名可能不叫"model"，尝试寻找ChatModel类型的字段
                for (Field field : chatClientClass.getDeclaredFields())
                {
                    field.setAccessible(true);
                    if (ChatModel.class.isAssignableFrom(field.getType()))
                    {
                        return (ChatModel) field.get(chatClient);
                    }
                }
                
                // 如果没找到，尝试找一个getModel方法
                try
                {
                    return (ChatModel) chatClientClass.getMethod("getModel").invoke(chatClient);
                } catch (NoSuchMethodException ex)
                {
                    log.error("ChatClient类中没有model字段或getModel方法", ex);
                    return null;
                }
            }
        } catch (Exception e)
        {
            log.error("反射获取ChatModel失败", e);
            return null;
        }
    }
    
    /**
     * 通过反射获取ChatClient中的默认Advisors
     * 
     * @param chatClient ChatClient对象
     * @return 提取出的默认Advisors列表，如果失败则返回空列表
     */
    @SuppressWarnings("unchecked")
    private static List<Advisor> extractDefaultAdvisorsFromChatClient(ChatClient chatClient)
    {
        try {
            // 获取ChatClient类
            Class<?> chatClientClass = chatClient.getClass();
            
            // 尝试获取defaultAdvisors字段
            try {
                Field advisorsField = chatClientClass.getDeclaredField("defaultAdvisors");
                advisorsField.setAccessible(true);
                Object advisorsObj = advisorsField.get(chatClient);
                if (advisorsObj instanceof List) {
                    return (List<Advisor>) advisorsObj;
                }
            } catch (NoSuchFieldException e) {
                // 尝试找其他可能的字段名
                for (Field field : chatClientClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getName().toLowerCase().contains("advisor") && field.get(chatClient) instanceof List) {
                        return (List<Advisor>) field.get(chatClient);
                    }
                }
                
                // 尝试找getDefaultAdvisors方法
                try {
                    Method getAdvisorsMethod = chatClientClass.getMethod("getDefaultAdvisors");
                    getAdvisorsMethod.setAccessible(true);
                    return (List<Advisor>) getAdvisorsMethod.invoke(chatClient);
                } catch (NoSuchMethodException ex) {
                    // 尝试其他可能的方法名
                    for (Method method : chatClientClass.getMethods()) {
                        if (method.getName().toLowerCase().contains("advisor") && 
                            List.class.isAssignableFrom(method.getReturnType())) {
                            method.setAccessible(true);
                            return (List<Advisor>) method.invoke(chatClient);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("反射获取默认Advisors失败", e);
        }
        
        // 如果所有尝试都失败，返回一个空列表
        return new ArrayList<>();
    }

    // 构建 advisors 方法
    private static List<Advisor> buildAdvisors(List<Advisor> additionalAdvisors)
    {
        List<Advisor> advisors = new ArrayList<>();
        advisors.add(new SimpleLoggerAdvisor()); // 添加默认的 logger advisor
        if (additionalAdvisors != null)
        {
            advisors.addAll(additionalAdvisors); // 将传入的额外 advisor 加入
        }
        return advisors;
    }
}
