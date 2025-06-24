package org.xiaoxingbomei.dao.localhost;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiaoxingbomei.vo.LlmSystemPrompt;

import java.util.List;

@Mapper
public interface PromptMapper
{
    /**
     * 查询所有提示词
     * @return 提示词列表
     */
    List<LlmSystemPrompt> getAllPrompts();
    
    /**
     * 根据ID查询提示词
     * @param promptId 提示词ID
     * @return 提示词信息
     */
    LlmSystemPrompt getPromptById(@Param("promptId") String promptId);
    
    /**
     * 添加提示词
     * @param prompt 提示词信息
     * @return 影响行数
     */
    int addPrompt(LlmSystemPrompt prompt);
    
    /**
     * 更新提示词
     * @param prompt 提示词信息
     * @return 影响行数
     */
    int updatePrompt(LlmSystemPrompt prompt);
    
    /**
     * 删除提示词
     * @param promptId 提示词ID
     * @return 影响行数
     */
    int deletePrompt(@Param("promptId") String promptId);
}
