package org.xiaoxingbomei.constant;

public class ApiConstant
{


    public class Chat
    {
        public static final String chat                  = "/ai/chat";                       // 对话
        public static final String chat_for_string       = "/ai/chat/chat_for_string";       // 普通对话
        public static final String chat_for_stream       = "/ai/chat/chat_for_stream";       // 流式对话

        public static final String getAllChatHistoryList = "/ai/chat/getAllChatHistoryList"; // 获取全部历史会话列表
        public static final String insertChatHistoryList = "/ai/chat/insertChatHistoryList"; // 新增一个历史会话
        public static final String deleteChatHistoryList = "/ai/chat/deleteChatHistoryList"; // 删除一个历史会话
        public static final String updateChatHistoryList = "/ai/chat/updateChatHistoryList"; // 更新一个历史会话

        public static final String getChatHistoryById    = "/ai/chat/getChatHistoryById";    // 根据id获取会话的历史记录详情
    }

    public class Game
    {
        public static final String chat_for_game       = "/ai/game/chat";
    }


    public class Service
    {
        public static final String chat_for_service    = "/ai/service/chat";
    }

    public class Prompt
    {
        public static final String getAllSystemPrompt   = "/ai/prompt/getAllSystemPrompt";    // 获取所有系统提示词
        public static final String getSystemPromptById  = "/ai/prompt/getSystemPromptById";   // 根据ID获取系统提示词
        public static final String addSystemPrompt      = "/ai/prompt/addSystemPrompt";       // 添加系统提示词
        public static final String deleteSystemPrompt   = "/ai/prompt/deleteSystemPrompt";    // 删除系统提示词
    }
    
    public class Model
    {
        public static final String getAllModels       = "/ai/model/getAllModels";           // 获取所有模型
        public static final String addModel           = "/ai/model/addModel";               // 添加模型
        public static final String updateModel        = "/ai/model/updateModel";            // 更新模型
        public static final String deleteModel        = "/ai/model/deleteModel";            // 删除模型
    }

}
