package org.xiaoxingbomei.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiaoxingbomei.dao.localhost.ModelMapper;
import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.service.LlmModelService;
import org.xiaoxingbomei.utils.Request_Utils;
import org.xiaoxingbomei.vo.LlmModel;

import java.util.List;

@Service
public class LlmModelServiceImpl implements LlmModelService
{
    @Autowired
    private ModelMapper modelMapper;
    
    /**
     * 获取所有模型
     * @return 模型列表
     */
    @Override
    public ResponseEntity getAllModels()
    {
        try
        {
            List<LlmModel> modelList = modelMapper.getAllModels();
            return ResponseEntity.success(modelList,"获取模型列表成功");
        } catch (Exception e) {
            return ResponseEntity.error("获取模型列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据提供者获取模型
     * @param provider 模型提供者
     * @return 模型列表
     */
    @Override
    public ResponseEntity getModelsByProvider(String provider) {
        try {
            List<LlmModel> modelList = modelMapper.getModelsByProvider(provider);
            return ResponseEntity.success(modelList);
        } catch (Exception e) {
            return ResponseEntity.error("获取模型列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加模型
     * @param model 模型信息
     * @return 添加结果
     */
    @Override
    public ResponseEntity addModel(LlmModel model) {
        try {
            // 参数校验
            if (model.getModelProvider() == null || model.getModelProvider().isEmpty() ||
                    model.getModelName() == null || model.getModelName().isEmpty()) {
                return ResponseEntity.error("模型提供者和名称不能为空");
            }
            
            int result = modelMapper.addModel(model);
            
            if (result > 0) {
                return ResponseEntity.success(model, "添加模型成功");
            } else {
                return ResponseEntity.error("添加模型失败");
            }
        } catch (Exception e) {
            return ResponseEntity.error("添加模型失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新模型
     * @param model 模型信息
     * @return 更新结果
     */
    @Override
    public ResponseEntity updateModel(LlmModel model) {
        try {
            // 参数校验
            if (model.getModelProvider() == null || model.getModelProvider().isEmpty() ||
                    model.getModelName() == null || model.getModelName().isEmpty()) {
                return ResponseEntity.error("模型提供者和名称不能为空");
            }
            
            int result = modelMapper.updateModel(model);
            
            if (result > 0) {
                return ResponseEntity.success(model, "更新模型成功");
            } else {
                return ResponseEntity.error("更新模型失败，模型可能不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.error("更新模型失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除模型
     * @param provider 模型提供者
     * @param name 模型名称
     * @return 删除结果
     */
    @Override
    public ResponseEntity deleteModel(String paramString)
    {
        String modelProvider = Request_Utils.getParam(paramString, "modelProvider");
        String modelName     = Request_Utils.getParam(paramString, "modelName");
        try
        {
            // 参数校验
            if (modelProvider == null || modelProvider.isEmpty() || modelName == null || modelName.isEmpty()) {
                return ResponseEntity.error("模型提供者和名称不能为空");
            }
            
            int result = modelMapper.deleteModel(modelProvider, modelName);
            
            if (result > 0) {
                return ResponseEntity.success("删除模型成功");
            } else {
                return ResponseEntity.error("删除模型失败，模型可能不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.error("删除模型失败：" + e.getMessage());
        }
    }
}
