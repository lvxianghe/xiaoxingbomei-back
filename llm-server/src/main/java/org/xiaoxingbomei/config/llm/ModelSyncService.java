package org.xiaoxingbomei.config.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiaoxingbomei.service.LlmModelService;
import org.xiaoxingbomei.vo.LlmModel;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 模型同步服务
 * 在应用启动时将配置文件中的模型信息同步到数据库
 */
@Service
@Slf4j
public class ModelSyncService {

    @Autowired
    private AiModelProperties aiModelProperties;

    @Autowired
    private LlmModelService llmModelService;

    /**
     * 应用启动后自动执行模型同步
     */
    @PostConstruct
    public void syncModelsToDatabase() {
        log.info("==============================================");
        log.info("           开始同步模型配置到数据库             ");
        log.info("==============================================");

        try {
            // 1. 获取数据库中现有的模型
            List<LlmModel> existingModels = getAllModelsFromDatabase();
            log.info("数据库中现有模型数量: {}", existingModels.size());

            // 2. 从配置中构建模型列表
            List<LlmModel> configModels = buildModelsFromConfig();
            log.info("配置文件中模型数量: {}", configModels.size());

            // 3. 同步模型（以配置为准）
            syncModels(existingModels, configModels);

            log.info("==============================================");
            log.info("           模型配置同步完成                   ");
            log.info("==============================================");

        } catch (Exception e) {
            log.error("模型配置同步失败", e);
        }
    }

    /**
     * 获取数据库中所有模型
     */
    private List<LlmModel> getAllModelsFromDatabase() {
        try {
            // 调用现有的获取所有模型的服务方法
            var response = llmModelService.getAllModels();
            log.debug("数据库查询响应: code={}, message={}", response.getCode(), response.getMessage());
            
            if (response.getCode().equals("200") && response.getData() instanceof List) {
                List<LlmModel> models = (List<LlmModel>) response.getData();
                
                // 详细打印每个模型
                log.info("=== 数据库中现有模型详情 ===");
                for (int i = 0; i < models.size(); i++) {
                    LlmModel model = models.get(i);
                    log.info("模型 {}: {}/{} - {}", 
                        i + 1, 
                        model.getModelProvider(), 
                        model.getModelName(), 
                        model.getModelDescription());
                }
                log.info("=== 数据库模型详情结束 ===");
                
                return models;
            }
        } catch (Exception e) {
            log.warn("获取数据库模型失败，可能是首次启动: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 从配置文件构建模型列表
     */
    private List<LlmModel> buildModelsFromConfig()
    {
        List<LlmModel> models = new ArrayList<>();

        // 处理OpenAI模型
        aiModelProperties.getOpenai().forEach((name, config) ->
        {
            LlmModel model = new LlmModel();
            model.setModelProvider("openai");
            model.setModelName(name);
            model.setModelDescription(config.getModelDescription());
            model.setModelTag(config.getModelTag());
            model.setEmbeddingModelName(config.getEmbeddingModelName());
            model.setEmbeddingDimensions(config.getEmbeddingDimensions() != null ? 
                config.getEmbeddingDimensions().intValue() : null);
            models.add(model);
            
            log.debug("配置中发现OpenAI模型: {} - {}", name, config.getModelDescription());
        });

        // 处理Ollama模型
        aiModelProperties.getOllama().forEach((name, config) ->
        {
            LlmModel model = new LlmModel();
            model.setModelProvider("ollama");
            model.setModelName(name);
            model.setModelDescription(config.getModelDescription());
            model.setModelTag(config.getModelTag());
            model.setEmbeddingModelName(config.getEmbeddingModelName());
            model.setEmbeddingDimensions(config.getEmbeddingDimensions() != null ? 
                config.getEmbeddingDimensions().intValue() : null);
            models.add(model);
            
            log.debug("配置中发现Ollama模型: {} - {}", name, config.getModelDescription());
        });

        return models;
    }

    /**
     * 同步模型到数据库（以配置为准）
     */
    private void syncModels(List<LlmModel> existingModels, List<LlmModel> configModels) {
        int addedCount = 0;
        int updatedCount = 0;

        for (LlmModel configModel : configModels)
        {
            // 查找数据库中是否存在相同的模型（根据provider和name）
            LlmModel existingModel = findExistingModel(existingModels, 
                configModel.getModelProvider(), configModel.getModelName());

            if (existingModel == null)
            {
                // 新增模型
                try {
                    llmModelService.addModel(configModel);
                    addedCount++;
                    log.info("新增模型: {} - {}", 
                        configModel.getModelProvider() + "/" + configModel.getModelName(),
                        configModel.getModelDescription());
                } catch (Exception e) {
                    log.error("新增模型失败: {} - {}", 
                        configModel.getModelProvider() + "/" + configModel.getModelName(), 
                        e.getMessage());
                }
            } else {
                // 更新模型（以配置为准）
                if (isModelChanged(existingModel, configModel)) {
                    try {
                        llmModelService.updateModel(configModel);
                        updatedCount++;
                        log.info("更新模型: {} - {}", 
                            configModel.getModelProvider() + "/" + configModel.getModelName(),
                            configModel.getModelDescription());
                    } catch (Exception e) {
                        log.error("更新模型失败: {} - {}", 
                            configModel.getModelProvider() + "/" + configModel.getModelName(), 
                            e.getMessage());
                    }
                }
            }
        }

        log.info("模型同步完成 - 新增: {}, 更新: {}", addedCount, updatedCount);
    }

    /**
     * 查找已存在的模型
     */
    private LlmModel findExistingModel(List<LlmModel> existingModels, 
                                       String provider, String name) {
        return existingModels.stream()
                .filter(model -> provider.equals(model.getModelProvider()) && 
                               name.equals(model.getModelName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 检查模型是否有变化
     */
    private boolean isModelChanged(LlmModel existing, LlmModel config)
    {
        return !equals(existing.getModelDescription(), config.getModelDescription()) ||
               !equals(existing.getModelTag(), config.getModelTag()) ||
               !equals(existing.getEmbeddingModelName(), config.getEmbeddingModelName()) ||
               !equals(existing.getEmbeddingDimensions(), config.getEmbeddingDimensions());
    }

    /**
     * 安全的对象比较
     */
    private boolean equals(Object a, Object b)
    {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
} 