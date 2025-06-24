package org.xiaoxingbomei.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;
import org.xiaoxingbomei.common.utils.Request_Utils;
import org.xiaoxingbomei.dao.localhost.ModelMapper;
import org.xiaoxingbomei.service.LlmModelService;
import org.xiaoxingbomei.vo.LlmModel;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class LlmModelServiceImpl implements LlmModelService
{
    // 暂时注释掉，因为会触发OpenAI自动配置
    // @Autowired
    // private OpenAiEmbeddingModel embeddingModel;
    
    @Autowired
    private ModelMapper modelMapper;

    
    /**
     * 获取所有模型
     * @return 模型列表
     */
    @Override
    public GlobalResponse getAllModels()
    {
        try
        {
            log.info("开始查询数据库中的所有模型...");
            List<LlmModel> modelList = modelMapper.getAllModels();
            log.info("数据库查询完成，返回模型数量: {}", modelList != null ? modelList.size() : "null");
            
            if (modelList != null && !modelList.isEmpty()) {
                log.info("查询到的模型列表:");
                for (int i = 0; i < modelList.size(); i++) {
                    LlmModel model = modelList.get(i);
                    log.info("  模型 {}: {}/{} - {}", 
                        i + 1, 
                        model.getModelProvider(), 
                        model.getModelName(), 
                        model.getModelDescription());
                }
            } else {
                log.warn("数据库查询结果为空或null");
            }
            
            return GlobalResponse.success(modelList,"获取模型列表成功");
        } catch (Exception e) {
            log.error("获取模型列表失败", e);
            return GlobalResponse.error("获取模型列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据提供者获取模型
     * @param provider 模型提供者
     * @return 模型列表
     */
    @Override
    public GlobalResponse getModelsByProvider(String provider) {
        try {
            List<LlmModel> modelList = modelMapper.getModelsByProvider(provider);
            return GlobalResponse.success(modelList);
        } catch (Exception e) {
            return GlobalResponse.error("获取模型列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加模型
     * @param model 模型信息
     * @return 添加结果
     */
    @Override
    public GlobalResponse addModel(LlmModel model) {
        try {
            // 参数校验
            if (model.getModelProvider() == null || model.getModelProvider().isEmpty() ||
                    model.getModelName() == null || model.getModelName().isEmpty()) {
                return GlobalResponse.error("模型提供者和名称不能为空");
            }
            
            int result = modelMapper.addModel(model);
            
            if (result > 0) {
                return GlobalResponse.success(model, "添加模型成功");
            } else {
                return GlobalResponse.error("添加模型失败");
            }
        } catch (Exception e) {
            return GlobalResponse.error("添加模型失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新模型
     * @param model 模型信息
     * @return 更新结果
     */
    @Override
    public GlobalResponse updateModel(LlmModel model) {
        try {
            // 参数校验
            if (model.getModelProvider() == null || model.getModelProvider().isEmpty() ||
                    model.getModelName() == null || model.getModelName().isEmpty()) {
                return GlobalResponse.error("模型提供者和名称不能为空");
            }
            
            int result = modelMapper.updateModel(model);
            
            if (result > 0) {
                return GlobalResponse.success(model, "更新模型成功");
            } else {
                return GlobalResponse.error("更新模型失败，模型可能不存在");
            }
        } catch (Exception e) {
            return GlobalResponse.error("更新模型失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除模型
     * @return 删除结果
     */
    @Override
    public GlobalResponse deleteModel(String paramString)
    {
        String modelProvider = Request_Utils.getParam(paramString, "modelProvider");
        String modelName     = Request_Utils.getParam(paramString, "modelName");
        try
        {
            // 参数校验
            if (modelProvider == null || modelProvider.isEmpty() || modelName == null || modelName.isEmpty()) {
                return GlobalResponse.error("模型提供者和名称不能为空");
            }
            
            int result = modelMapper.deleteModel(modelProvider, modelName);
            
            if (result > 0) {
                return GlobalResponse.success("删除模型成功");
            } else {
                return GlobalResponse.error("删除模型失败，模型可能不存在");
            }
        } catch (Exception e) {
            return GlobalResponse.error("删除模型失败：" + e.getMessage());
        }
    }


}
