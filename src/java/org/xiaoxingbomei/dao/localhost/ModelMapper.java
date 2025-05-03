package org.xiaoxingbomei.dao.localhost;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiaoxingbomei.vo.LlmModel;

import java.util.List;

@Mapper
public interface ModelMapper
{
    /**
     * 获取所有模型
     * @return 模型列表
     */
    List<LlmModel> getAllModels();
    
    /**
     * 根据提供者获取模型
     * @param provider 模型提供者
     * @return 模型列表
     */
    List<LlmModel> getModelsByProvider(@Param("provider") String provider);
    
    /**
     * 添加模型
     * @param model 模型信息
     * @return 影响行数
     */
    int addModel(LlmModel model);
    
    /**
     * 更新模型
     * @param model 模型信息
     * @return 影响行数
     */
    int updateModel(LlmModel model);
    
    /**
     * 删除模型
     * @param provider 模型提供者
     * @param name 模型名称
     * @return 影响行数
     */
    int deleteModel(@Param("modelProvider") String modelProvider, @Param("modelName") String modelName);
} 