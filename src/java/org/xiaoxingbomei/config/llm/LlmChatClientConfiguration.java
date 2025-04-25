package org.xiaoxingbomei.config.llm;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.xiaoxingbomei.constant.SystemPromptConstant;
import org.xiaoxingbomei.model.AlibabaOpenAiChatModel;
import org.xiaoxingbomei.tools.ProgrammerTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 大模型应用对话的配置
 */
@Configuration
public class LlmChatClientConfiguration
{
    /**
     * 配置会话记忆
     */
    @Bean
    public ChatMemory chatMemory()
    {
        return new InMemoryChatMemory();
    }

    /**
     * 配置ollama的chatClient
     */
    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel model, ChatMemory chatMemory)
    {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemPromptConstant.XIAOXINGBOMEI_SYSTEM_PROMPT)
                .defaultAdvisors
                        (
                                new SimpleLoggerAdvisor(),               // 日志增强
                                new MessageChatMemoryAdvisor(chatMemory) // 会话记忆增强
                        )
                .build();
    }

    /**
     * 配置openai的chatClient
     */
    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel model,ChatMemory chatMemory)
    {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemPromptConstant.GAME_SYSTEM_PROMPT)
                .defaultAdvisors
                        (
                                new SimpleLoggerAdvisor(),               // 日志增强
                                new MessageChatMemoryAdvisor(chatMemory) // 会话记忆增强
                        )
                .build();
    }

    /**
     * 配置智能客服的chatClient
     */
    @Bean
//    public ChatClient serviceChatClient(AlibabaOpenAiChatModel model, ChatMemory chatMemory, ProgrammerTools programmerTools)
//    public ChatClient serviceChatClient(OllamaChatModel model, ChatMemory chatMemory, ProgrammerTools programmerTools)
    public ChatClient serviceChatClient(OpenAiChatModel model, ChatMemory chatMemory, ProgrammerTools programmerTools)
    {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemPromptConstant.SERVICE_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .defaultTools(programmerTools)
                .build();
    }

    @Bean
    public AlibabaOpenAiChatModel alibabaOpenAiChatModel(OpenAiConnectionProperties commonProperties, OpenAiChatProperties chatProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider, ObjectProvider<WebClient.Builder> webClientBuilderProvider, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler, ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<ChatModelObservationConvention> observationConvention) {
        String baseUrl = StringUtils.hasText(chatProperties.getBaseUrl()) ? chatProperties.getBaseUrl() : commonProperties.getBaseUrl();
        String apiKey = StringUtils.hasText(chatProperties.getApiKey()) ? chatProperties.getApiKey() : commonProperties.getApiKey();
        String projectId = StringUtils.hasText(chatProperties.getProjectId()) ? chatProperties.getProjectId() : commonProperties.getProjectId();
        String organizationId = StringUtils.hasText(chatProperties.getOrganizationId()) ? chatProperties.getOrganizationId() : commonProperties.getOrganizationId();
        Map<String, List<String>> connectionHeaders = new HashMap<>();
        if (StringUtils.hasText(projectId)) {
            connectionHeaders.put("OpenAI-Project", List.of(projectId));
        }

        if (StringUtils.hasText(organizationId)) {
            connectionHeaders.put("OpenAI-Organization", List.of(organizationId));
        }
        RestClient.Builder restClientBuilder = restClientBuilderProvider.getIfAvailable(RestClient::builder);
        WebClient.Builder webClientBuilder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(new SimpleApiKey(apiKey)).headers(CollectionUtils.toMultiValueMap(connectionHeaders)).completionsPath(chatProperties.getCompletionsPath()).embeddingsPath("/v1/embeddings").restClientBuilder(restClientBuilder).webClientBuilder(webClientBuilder).responseErrorHandler(responseErrorHandler).build();
        AlibabaOpenAiChatModel chatModel = AlibabaOpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(chatProperties.getOptions()).toolCallingManager(toolCallingManager).retryTemplate(retryTemplate).observationRegistry((ObservationRegistry) observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).build();
        Objects.requireNonNull(chatModel);
        observationConvention.ifAvailable(chatModel::setObservationConvention);
        return chatModel;
    }

}
