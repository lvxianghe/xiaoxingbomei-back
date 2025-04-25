package org.xiaoxingbomei.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.*;

import java.util.List;
import java.util.Map;

/**
 * llm模型单次对话的对象实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LLmChat
{
    MessageType messageType;
    String text;
    Map<String,Object> metadata;
    List<AssistantMessage.ToolCall> toolCalls;

    public LLmChat(Message message)
    {
        this.messageType = message.getMessageType();
        this.text        = message.getText();
        this.metadata    = message.getMetadata();
        if(message instanceof AssistantMessage am)
        {
            this.toolCalls = am.getToolCalls();
        }
    }

    public Message toMessage()
    {
        return switch (messageType)
        {
            case SYSTEM    -> new SystemMessage(text);
            case USER      -> new UserMessage(text, List.of(), metadata);
            case ASSISTANT -> new AssistantMessage(text, metadata, toolCalls, List.of());
            default        -> throw new IllegalArgumentException("Unsupported message type: " + messageType);
        };
    }
}
