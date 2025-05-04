package org.xiaoxingbomei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xiaoxingbomei.constant.ApiConstant;
import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.service.ChatService;
import org.xiaoxingbomei.service.LlmModelService;
import org.xiaoxingbomei.service.PromptService;
import org.xiaoxingbomei.vo.LlmChatHistory;
import org.xiaoxingbomei.vo.LlmModel;
import org.xiaoxingbomei.vo.LlmSystemPrompt;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class AiController
{
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private PromptService promptService;
    
    @Autowired
    private LlmModelService llmModelService;

    // =========================================================

    @RequestMapping(value = ApiConstant.Chat.chat,method = RequestMethod.GET,produces ="text/html;charset=utf-8")
    public Flux<String> chat
            (
            @RequestParam(value = "prompt")   String prompt,
            @RequestParam(value = "chatId")   String chatId,
            @RequestParam(value = "isStream") String isStream,
            @RequestParam(value = "modelName") String modelName,
            @RequestParam(value = "modelProvider") String modelProvider,
            @RequestParam(value = "systemPrompt") String systemPrompt
            )
    {

        Flux<String> ret = chatService.chat(prompt,chatId,isStream,modelProvider,modelName,systemPrompt);

        return ret;
    }


    @RequestMapping(value = ApiConstant.Chat.chat_for_string, method = RequestMethod.GET)
    public ResponseEntity chat_for_string(@RequestParam(value = "prompt") String prompt)
    {
        ResponseEntity ret = null;

        ret = chatService.chat_for_string(prompt);

        return ret;
    }
    @RequestMapping(value = ApiConstant.Chat.chat_for_stream, method = RequestMethod.GET,produces ="text/html;charset=utf-8")
    public Flux<String> chat_for_stream(
            @RequestParam(value = "prompt") String prompt,
            @RequestParam(value = "chatId") String chatId
    )
    {
        Flux<String> ret = null;

        ret = chatService.chat_for_stream(prompt,chatId);

        return ret;
    }

    @RequestMapping(value = ApiConstant.Chat.getAllChatHistoryList, method = RequestMethod.GET)
    public ResponseEntity getAllChatHistoryList()
    {
        ResponseEntity ret = null;

        ret = chatService.getAllChatHistoryList();

        return ret;
    }

    @RequestMapping(value = ApiConstant.Chat.insertChatHistoryList, method = RequestMethod.POST)
    public ResponseEntity insertChatHistoryList(@RequestBody String paramString)
    {
        ResponseEntity ret = null;

        ret = chatService.insertChatHistoryList(paramString);

        return ret;
    }

    @RequestMapping(value = ApiConstant.Chat.deleteChatHistoryList, method = RequestMethod.POST)
    public ResponseEntity deleteChatHistoryList(@RequestBody String paramString)
    {
        ResponseEntity ret = null;

        ret = chatService.deleteChatHistoryList(paramString);

        return ret;
    }

    @RequestMapping(value = ApiConstant.Chat.updateChatHistoryList, method = RequestMethod.POST)
    public ResponseEntity updateChatHistoryList(@RequestBody String paramString)
    {
        ResponseEntity ret = null;

        ret = chatService.updateChatHistoryList(paramString);

        return ret;
    }

    @RequestMapping(value = ApiConstant.Chat.getChatHistoryById, method = RequestMethod.GET)
    public List<LlmChatHistory> getChatHistoryById(@RequestParam(value = "chatId") String chatId)
    {
        return chatService.getChatHistoryById(chatId);

    }

    @RequestMapping(value = ApiConstant.Game.chat_for_game,method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public Flux<String> chat_for_game(String prompt, String chatId)
    {
        return chatService.chat_for_game(prompt, chatId);
    }

    @RequestMapping(value = ApiConstant.Service.chat_for_service,method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String chat_for_service(String prompt, String chatId)
    {
        return chatService.chat_for_service(prompt, chatId);
    }

    /**
     * 获取所有系统提示词
     * @return 提示词列表
     */
    @RequestMapping(value = ApiConstant.Prompt.getAllSystemPrompt, method = RequestMethod.POST)
    public ResponseEntity getAllSystemPrompt()
    {
        ResponseEntity ret = null;
        
        ret = promptService.getAllSystemPrompt();
        
        return ret;
    }
    
    /**
     * 根据ID获取系统提示词
     * @param promptId 提示词ID
     * @return 提示词信息
     */
    @RequestMapping(value = ApiConstant.Prompt.getSystemPromptById, method = RequestMethod.POST)
    public ResponseEntity getSystemPromptById(@RequestBody String paramString)
    {
        ResponseEntity ret = null;
        
        ret = promptService.getSystemPromptById(paramString);
        
        return ret;
    }

    /**
     * 添加系统提示词
     * @param prompt 提示词信息
     * @return 添加结果
     */
    @RequestMapping(value = ApiConstant.Prompt.addSystemPrompt, method = RequestMethod.POST)
    public ResponseEntity addSystemPrompt(@RequestBody LlmSystemPrompt prompt)
    {
        ResponseEntity ret = null;
        
        ret = promptService.addSystemPrompt(prompt);
        
        return ret;
    }
    
    /**
     * 删除系统提示词
     */
    @RequestMapping(value = ApiConstant.Prompt.deleteSystemPrompt, method = RequestMethod.POST)
    public ResponseEntity deleteSystemPrompt(@RequestBody String paramString)
    {
        ResponseEntity ret = null;
        
        ret = promptService.deleteSystemPrompt(paramString);
        
        return ret;
    }

    /**
     * 获取所有模型
     * @return 模型列表
     */
    @RequestMapping(value = ApiConstant.Model.getAllModels, method = RequestMethod.POST)
    public ResponseEntity getAllModels()
    {
        ResponseEntity ret = llmModelService.getAllModels();
        
        return ret;
    }

    
    /**
     * 添加模型
     * @param model 模型信息
     * @return 添加结果
     */
    @RequestMapping(value = ApiConstant.Model.addModel, method = RequestMethod.POST)
    public ResponseEntity addModel(@RequestBody LlmModel model)
    {
        ResponseEntity ret = null;
        
        ret = llmModelService.addModel(model);
        
        return ret;
    }
    
    /**
     * 更新模型
     * @param model 模型信息
     * @return 更新结果
     */
    @RequestMapping(value = ApiConstant.Model.updateModel, method = RequestMethod.POST)
    public ResponseEntity updateModel(@RequestBody LlmModel model)
    {
        ResponseEntity ret = null;
        
        ret = llmModelService.updateModel(model);
        
        return ret;
    }
    
    /**
     * 删除模型
     * @param modelProvider 模型提供者
     * @param modelName 模型名称
     * @return 删除结果
     */
    @RequestMapping(value = ApiConstant.Model.deleteModel, method = RequestMethod.POST)
    public ResponseEntity deleteModel(@RequestBody String paramString)
    {
        ResponseEntity ret = llmModelService.deleteModel(paramString);
        
        return ret;
    }



}
