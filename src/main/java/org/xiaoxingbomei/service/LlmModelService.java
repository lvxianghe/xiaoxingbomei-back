package org.xiaoxingbomei.service;

import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.vo.LlmModel;

/**
 * 大模型服务
 */
public interface LlmModelService
{
    /**
     * 获取所有模型
     * @return 模型列表
     */
    ResponseEntity getAllModels();
    
    /**
     * 根据提供者获取模型
     * @param provider 模型提供者
     * @return 模型列表
     */
    ResponseEntity getModelsByProvider(String provider);
    
    /**
     * 添加模型
     * @param model 模型信息
     * @return 添加结果
     */
    ResponseEntity addModel(LlmModel model);
    
    /**
     * 更新模型
     * @param model 模型信息
     * @return 更新结果
     */
    ResponseEntity updateModel(LlmModel model);
    
    /**
     * 删除模型
     * @param provider 模型提供者
     * @param name 模型名称
     * @return 删除结果
     */
    ResponseEntity deleteModel(String paramString);

}
