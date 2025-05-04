package org.xiaoxingbomei.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiaoxingbomei.constant.SystemPromptConstant;
import org.xiaoxingbomei.dao.localhost.PromptMapper;
import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.service.PromptService;
import org.xiaoxingbomei.utils.Request_Utils;
import org.xiaoxingbomei.vo.LlmSystemPrompt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PromptServiceImpl implements PromptService
{
    @Autowired
    private PromptMapper promptMapper;

    /**
     * 获取所有系统提示词
     * @return 提示词列表
     */
    @Override
    public ResponseEntity getAllSystemPrompt()
    {
        try {
            List<LlmSystemPrompt> promptList = promptMapper.getAllPrompts();
            
            // 如果数据库中没有数据，返回默认的提示词
            if (promptList == null || promptList.isEmpty()) {
                promptList = getDefaultPrompts();
            }
            
            return ResponseEntity.success(promptList);
        } catch (Exception e) {
            return ResponseEntity.error("获取系统提示词失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取系统提示词
     * @param promptId 提示词ID
     * @return 提示词信息
     */
    @Override
    public ResponseEntity getSystemPromptById(String paramString)
    {
        String promptId = Request_Utils.getParam(paramString, "promptId");
        try {
            LlmSystemPrompt prompt = promptMapper.getPromptById(promptId);
            
            if (prompt != null) {
                return ResponseEntity.success(prompt);
            } else {
                // 如果数据库中没有找到，尝试从默认提示词中查找
                List<LlmSystemPrompt> defaultPrompts = getDefaultPrompts();
                for (LlmSystemPrompt defaultPrompt : defaultPrompts) {
                    if (defaultPrompt.getPromptId().equals(promptId)) {
                        return ResponseEntity.success(defaultPrompt);
                    }
                }
                return ResponseEntity.error("未找到对应的提示词");
            }
        } catch (Exception e) {
            return ResponseEntity.error("获取系统提示词失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加系统提示词
     * @param prompt 提示词信息
     * @return 添加结果
     */
    @Override
    public ResponseEntity addSystemPrompt(LlmSystemPrompt prompt) {
        try {
            // 如果没有指定ID，生成一个UUID
            if (prompt.getPromptId() == null || prompt.getPromptId().isEmpty()) {
                prompt.setPromptId(UUID.randomUUID().toString().replace("-", ""));
            }
            
            // 如果没有指定状态，默认为启用
            if (prompt.getStatus() == null || prompt.getStatus().isEmpty()) {
                prompt.setStatus("1");
            }
            
            int result = promptMapper.addPrompt(prompt);
            
            if (result > 0) {
                return ResponseEntity.success(prompt, "添加提示词成功");
            } else {
                return ResponseEntity.error("添加提示词失败");
            }
        } catch (Exception e) {
            return ResponseEntity.error("添加系统提示词失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除系统提示词
     * @param promptId 提示词ID
     * @return 删除结果
     */
    @Override
    public ResponseEntity deleteSystemPrompt(String paramString)
    {
        String promptId = Request_Utils.getParam(paramString, "promptId");
        try {
            // 先查询是否存在
            LlmSystemPrompt prompt = promptMapper.getPromptById(promptId);
            if (prompt == null) {
                return ResponseEntity.error("提示词不存在");
            }
            
            int result = promptMapper.deletePrompt(promptId);
            
            if (result > 0) {
                return ResponseEntity.success("删除提示词成功");
            } else {
                return ResponseEntity.error("删除提示词失败");
            }
        } catch (Exception e) {
            return ResponseEntity.error("删除系统提示词失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取默认提示词列表
     * @return 默认提示词列表
     */
    private List<LlmSystemPrompt> getDefaultPrompts() {
        List<LlmSystemPrompt> promptList = new ArrayList<>();
        
        // 小型博美提示词
        LlmSystemPrompt xiaoxingbomei = new LlmSystemPrompt();
        xiaoxingbomei.setPromptId("1");
        xiaoxingbomei.setPromptName("小型博美");
        xiaoxingbomei.setPromptType("基础对话");
        xiaoxingbomei.setPromptDescription("基础对话助手");
        xiaoxingbomei.setPromptTag("助手,对话");
        xiaoxingbomei.setPromptContent(SystemPromptConstant.XIAOXINGBOMEI_SYSTEM_PROMPT);
        xiaoxingbomei.setStatus("1");
        promptList.add(xiaoxingbomei);
        
        // 哄哄模拟器提示词
        LlmSystemPrompt game = new LlmSystemPrompt();
        game.setPromptId("2");
        game.setPromptName("哄哄模拟器");
        game.setPromptType("游戏");
        game.setPromptDescription("模拟哄男/女友的互动游戏");
        game.setPromptTag("游戏,互动,模拟");
        game.setPromptContent(SystemPromptConstant.GAME_SYSTEM_PROMPT);
        game.setStatus("1");
        promptList.add(game);
        
        // 智能客服提示词
        LlmSystemPrompt service = new LlmSystemPrompt();
        service.setPromptId("3");
        service.setPromptName("智能客服");
        service.setPromptType("客服");
        service.setPromptDescription("职业猎头公司智能客服");
        service.setPromptTag("客服,咨询,预约");
        service.setPromptContent(SystemPromptConstant.SERVICE_SYSTEM_PROMPT);
        service.setStatus("1");
        promptList.add(service);
        
        return promptList;
    }
}
