package org.xiaoxingbomei.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoxingbomei.dao.localhost.ModelMapper;
import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.service.FileService;
import org.xiaoxingbomei.service.LlmModelService;
import org.xiaoxingbomei.utils.Request_Utils;
import org.xiaoxingbomei.vo.LlmModel;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class LlmModelServiceImpl implements LlmModelService
{
    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;
    
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

    @Override
    public ResponseEntity uploadFile(String chatId, MultipartFile file)
    {
        try
        {
            // 1. 校验校验文件格式

            // 2.保存文件
            boolean success = fileService.saveFileWithChatId(chatId, file.getResource());
            if (!success)
            {
                return ResponseEntity.error("保存文件失败");
            }
            // 3.写入向量库
            this.writeToVectorStore(file.getResource());
            return ResponseEntity.success("保存文件成功");
        } catch (Exception e)
        {
            log.error("Failed to upload PDF.", e);
            return ResponseEntity.error("上传文件失败！");
        }
    }

    private void writeToVectorStore(Resource resource)
    {
        // 1.创建PDF的读取器
        PagePdfDocumentReader reader = new PagePdfDocumentReader
                (
                resource, // 文件源
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1) // 每1页PDF作为一个Document
                        .build()
        );
        // 2.读取PDF文档，拆分为Document
        List<Document> documents = reader.read();
        // 3.写入向量库
        vectorStore.add(documents);
    }

    @Override
    public org.springframework.http.ResponseEntity<Resource> downloadFile(String paramString)
    {
        log.info("下载文件，参数：{}", paramString);
        
        // 获取前端参数
        String chatId = Request_Utils.getParam(paramString, "chatId");
        if (chatId == null || chatId.isEmpty()) {
            log.error("下载文件失败：chatId为空");
            return org.springframework.http.ResponseEntity.badRequest().build();
        }
        
        log.info("下载文件，chatId：{}", chatId);
        
        // 1.读取文件
        Resource resource = fileService.getFileByChatId(chatId);
        if (resource == null) {
            log.error("下载文件失败：文件资源为null");
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        
        if (!resource.exists()) {
            log.error("下载文件失败：文件不存在");
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        
        try {
            log.info("文件存在，大小：{} 字节", resource.contentLength());
            
            // 2.文件名编码，写入响应头
            String filename = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
            log.info("文件名：{}", filename);
            
            // 3.返回文件
            return org.springframework.http.ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("下载文件异常：", e);
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }


}
