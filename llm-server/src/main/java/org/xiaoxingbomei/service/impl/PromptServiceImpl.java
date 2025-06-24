package org.xiaoxingbomei.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;
import org.xiaoxingbomei.common.utils.Request_Utils;
import org.xiaoxingbomei.constant.SystemPromptConstant;
import org.xiaoxingbomei.dao.localhost.PromptMapper;
import org.xiaoxingbomei.service.PromptService;
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
    public GlobalResponse getAllSystemPrompt()
    {
        try {
            List<LlmSystemPrompt> promptList = promptMapper.getAllPrompts();
            
            return GlobalResponse.success(promptList);
        } catch (Exception e) {
            return GlobalResponse.error("获取系统提示词失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取系统提示词
     * @return 提示词信息
     */
    @Override
    public GlobalResponse getSystemPromptById(String paramString)
    {
        String promptId = Request_Utils.getParam(paramString, "promptId");
        try {
            LlmSystemPrompt prompt = promptMapper.getPromptById(promptId);
            
            if (prompt != null) {
                return GlobalResponse.success(prompt);
            } else {

                return GlobalResponse.error("未找到对应的提示词");
            }
        } catch (Exception e) {
            return GlobalResponse.error("获取系统提示词失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加系统提示词
     * @param prompt 提示词信息
     * @return 添加结果
     */
    @Override
    public GlobalResponse addSystemPrompt(LlmSystemPrompt prompt) {
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
                return GlobalResponse.success(prompt, "添加提示词成功");
            } else {
                return GlobalResponse.error("添加提示词失败");
            }
        } catch (Exception e) {
            return GlobalResponse.error("添加系统提示词失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新系统提示词
     * @param prompt 提示词信息
     * @return 更新结果
     */
    @Override
    public GlobalResponse updateSystemPrompt(LlmSystemPrompt prompt) {
        try {
            // 验证必填字段
            if (prompt.getPromptId() == null || prompt.getPromptId().isEmpty()) {
                return GlobalResponse.error("提示词ID不能为空");
            }
            
            // 先查询是否存在
            LlmSystemPrompt existingPrompt = promptMapper.getPromptById(prompt.getPromptId());
            if (existingPrompt == null) {
                return GlobalResponse.error("提示词不存在，无法更新");
            }
            
            // 如果没有指定状态，保持原状态
            if (prompt.getStatus() == null || prompt.getStatus().isEmpty()) {
                prompt.setStatus(existingPrompt.getStatus());
            }
            
            int result = promptMapper.updatePrompt(prompt);
            
            if (result > 0) {
                return GlobalResponse.success(prompt, "更新提示词成功");
            } else {
                return GlobalResponse.error("更新提示词失败");
            }
        } catch (Exception e) {
            return GlobalResponse.error("更新系统提示词失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除系统提示词
     * @return 删除结果
     */
    @Override
    public GlobalResponse deleteSystemPrompt(String paramString)
    {
        String promptId = Request_Utils.getParam(paramString, "promptId");
        try {
            // 先查询是否存在
            LlmSystemPrompt prompt = promptMapper.getPromptById(promptId);
            if (prompt == null) {
                return GlobalResponse.error("提示词不存在");
            }
            
            int result = promptMapper.deletePrompt(promptId);
            
            if (result > 0) {
                return GlobalResponse.success("删除提示词成功");
            } else {
                return GlobalResponse.error("删除提示词失败");
            }
        } catch (Exception e) {
            return GlobalResponse.error("删除系统提示词失败：" + e.getMessage());
        }
    }
    

}
