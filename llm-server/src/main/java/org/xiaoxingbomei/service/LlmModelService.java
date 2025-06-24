package org.xiaoxingbomei.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;
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
    GlobalResponse getAllModels();
    
    /**
     * 根据提供者获取模型
     * @param provider 模型提供者
     * @return 模型列表
     */
    GlobalResponse getModelsByProvider(String provider);
    
    /**
     * 添加模型
     * @param model 模型信息
     * @return 添加结果
     */
    GlobalResponse addModel(LlmModel model);
    
    /**
     * 更新模型
     * @param model 模型信息
     * @return 更新结果
     */
    GlobalResponse updateModel(LlmModel model);


    /**
     * 删除模型
     * @param provider 模型提供者
     * @param name 模型名称
     * @return 删除结果
     */
    GlobalResponse deleteModel(String paramString);
}
