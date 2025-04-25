package org.xiaoxingbomei.config.llm;//package org.xiaoxingbomei.config;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenAiChatClientConfiguration
//{
//    @Bean
//    public ChatClient openAiChatClient(OpenAiChatModel model)
//    {
//        return ChatClient
//                .builder(model)
//                .defaultSystem("你是一个热心的智能助手，你的名字是小型博美，请以小型博美的身份回答问题")
//                .build();
//    }
//}