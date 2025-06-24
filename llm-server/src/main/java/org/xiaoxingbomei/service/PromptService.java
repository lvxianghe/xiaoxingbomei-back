package org.xiaoxingbomei.service;


import org.xiaoxingbomei.common.entity.response.GlobalResponse;
import org.xiaoxingbomei.vo.LlmSystemPrompt;

/**
 * 提示词
 */
public interface PromptService
{
    /**
     * 获取所有系统提示词
     * @return 提示词列表
     */
    GlobalResponse getAllSystemPrompt();

    /**
     * 根据ID获取系统提示词
     * @return 提示词信息
     */
    GlobalResponse getSystemPromptById(String paramString);
    
    /**
     * 添加系统提示词
     * @param prompt 提示词信息
     * @return 添加结果
     */
    GlobalResponse addSystemPrompt(LlmSystemPrompt prompt);
    
    /**
     * 更新系统提示词
     * @param prompt 提示词信息
     * @return 更新结果
     */
    GlobalResponse updateSystemPrompt(LlmSystemPrompt prompt);
    
    /**
     * 删除系统提示词
     * @param promptId 提示词ID
     * @return 删除结果
     */
    GlobalResponse deleteSystemPrompt(String promptId);
}
