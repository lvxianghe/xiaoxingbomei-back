package org.xiaoxingbomei.service;

import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.vo.LlmSystemPrompt;

import java.util.List;

/**
 * 提示词
 */
public interface PromptService
{
    /**
     * 获取所有系统提示词
     * @return 提示词列表
     */
    ResponseEntity getAllSystemPrompt();

    /**
     * 根据ID获取系统提示词
     * @param promptId 提示词ID
     * @return 提示词信息
     */
    ResponseEntity getSystemPromptById(String paramString);
    
    /**
     * 添加系统提示词
     * @param prompt 提示词信息
     * @return 添加结果
     */
    ResponseEntity addSystemPrompt(LlmSystemPrompt prompt);
    
    /**
     * 删除系统提示词
     * @param promptId 提示词ID
     * @return 删除结果
     */
    ResponseEntity deleteSystemPrompt(String promptId);
}
