package org.xiaoxingbomei.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.*;

import static org.springframework.ai.chat.messages.MessageType.SYSTEM;


/**
 * llm会话历史记录
 */
@NoArgsConstructor
@Data
public class LlmChatHistory
{
    private String chatId;      // 会话id
    private String chatRole;    // 会话角色
    private String chatContent; // 会话内容

    /**
     * Message 转 LlmChatHistory
     * MessageType是枚举，我们只需要 user 和 assistant 两种
     */
    public LlmChatHistory(Message message, String chatId)
    {
        switch (message.getMessageType())
        {
            case USER:
                this.chatRole = "user";
                break;
            case ASSISTANT:
                this.chatRole = "assistant";
                break;
            // case SYSTEM:
            //     this.chatRole = "system";
            //     break;
            // case TOOL:
            //     this.chatRole = "tool";
            //     break;
            default:
                this.chatRole = "unknown";
                break;
        }
        this.chatContent = message.getText();
        this.chatId      = chatId;
    }


    /**
     * LlmChatHistory 转 Message
     * MessageType是枚举，我们只需要 user 和 assistant 两种
     */
    public static Message toMessage(LlmChatHistory llmChatHistory)
    {
        // 根据chatRole 提取 messageType
        MessageType messageType = switch(llmChatHistory.getChatRole())
        {
            case    "system"    -> SYSTEM;
            case    "user"      -> MessageType.USER;
            case    "assistant" -> MessageType.ASSISTANT;
            default             -> throw new IllegalStateException("Unexpected value: " + llmChatHistory.getChatRole());
        };

        return switch (messageType)
        {
            case    SYSTEM     -> new SystemMessage(llmChatHistory.getChatContent());
            case    USER       -> new UserMessage(llmChatHistory.getChatContent());
            case    ASSISTANT  -> new AssistantMessage(llmChatHistory.getChatContent());
            default            -> throw new IllegalArgumentException("Unsupported message type");
        };

    }



}
