package org.xiaoxingbomei.constant;

public class ApiConstant
{


    public class Chat
    {
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

}
