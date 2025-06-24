package org.xiaoxingbomei.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;
import org.xiaoxingbomei.common.utils.Request_Utils;
import org.xiaoxingbomei.service.FileService;


import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class FileServiceImpl implements FileService
{
    @Autowired
    private VectorStore vectorStore;

    // 会话id 与 文件名的对应关系，方便查询会话历史时重新加载文件
    private final Properties chatFiles = new Properties();

    @Override
    public GlobalResponse uploadFile(String chatId, MultipartFile file)
    {
        try
        {
            // 1. 校验文件格式
            if (!validatePdfFile(file)) {
                return GlobalResponse.error("文件格式不支持，目前仅支持PDF文件");
            }

            // 2. 保存文件
            boolean success = saveFileWithChatId(chatId, file.getResource());
            if (!success)
            {
                return GlobalResponse.error("保存文件失败");
            }
            
            // 3. 解析文档并写入向量数据库
            this.parseAndStoreToVectorDatabase(file.getResource(), chatId);
            
            log.info("✅ [RAG] 文件上传成功: chatId={}, filename={}", chatId, file.getOriginalFilename());
            return GlobalResponse.success("文件上传成功，已启用RAG增强对话");
        } catch (Exception e)
        {
            log.error("❌ [RAG] 文件上传失败, chatId={}", chatId, e);
            return GlobalResponse.error("上传文件失败：" + e.getMessage());
        }
    }

    /**
     * 校验PDF文件格式
     */
    private boolean validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("文件为空");
            return false;
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            log.error("文件格式不支持，只支持PDF格式: {}", originalFilename);
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            log.error("文件MIME类型不正确: {}", contentType);
            return false;
        }
        
        // 检查文件大小（限制为50MB）
        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.getSize() > maxSize) {
            log.error("文件大小超过限制: {} bytes, 最大允许: {} bytes", file.getSize(), maxSize);
            return false;
        }
        
        log.info("✅ PDF文件格式校验通过: {}, 大小: {} bytes", originalFilename, file.getSize());
        return true;
    }

    /**
     * 解析PDF文档并存储到向量数据库
     */
    private void parseAndStoreToVectorDatabase(Resource resource, String chatId)
    {
        try {
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
            
            // 3.为每个Document添加chatId元数据
            documents.forEach(doc -> {
                doc.getMetadata().put("chatId", chatId);
                doc.getMetadata().put("filename", resource.getFilename());
                doc.getMetadata().put("uploadTime", LocalDateTime.now().toString());
                log.debug("🔍 [Vector] 文档片段: {}", doc.getText().substring(0, Math.min(100, doc.getText().length())));
            });
            
            // 4.存储到向量数据库
        vectorStore.add(documents);
            
            log.info("✅ [Vector] 成功存储到向量数据库: chatId={}, 文档片段数量={}", chatId, documents.size());
            
        } catch (Exception e) {
            log.error("❌ [Vector] 向量化存储失败: chatId={}", chatId, e);
            throw new RuntimeException("向量化处理失败", e);
        }
    }

    /**
     * 根据chatId和查询内容进行向量检索
     */
    @Override
    public List<String> searchDocumentContent(String chatId, String query, int maxResults) {
        try {
            // 构建向量搜索请求，限制在特定chatId的文档中搜索
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(maxResults)
                    .similarityThreshold(0.6) // 相似度阈值
                    .filterExpression("chatId == '" + chatId + "'") // 只搜索特定会话的文档
                    .build();
            
            // 执行向量相似性搜索
            List<Document> relevantDocs = vectorStore.similaritySearch(searchRequest);
            
            // 提取文档内容
            List<String> documentTexts = relevantDocs.stream()
                    .map(Document::getText)
                    .filter(text -> text != null && !text.trim().isEmpty())
                    .toList();
            
            log.info("🔍 [RAG] 向量检索完成: chatId={}, query={}, 找到相关文档数量={}", 
                chatId, query.length() > 50 ? query.substring(0, 50) + "..." : query, documentTexts.size());
            
            return documentTexts;
        } catch (Exception e) {
            log.error("❌ [RAG] 向量检索失败: chatId={}, query={}", chatId, query, e);
            return Collections.emptyList();
        }
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
        Resource resource = getFileByChatId(chatId);
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

    @Override
    public boolean saveFileWithChatId(String chatId, Resource resource)
    {
        try {
            // 1.获取文件名
            String filename = resource.getFilename();
            if (filename == null || filename.isEmpty()) {
                log.error("保存文件失败：文件名为空");
                return false;
            }
            
            // 2.创建保存目录
            File saveDir = new File("./uploads");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            
            // 3.为避免文件名冲突，使用chatId_filename格式
            String safeFilename = chatId + "_" + filename;
            File target = new File(saveDir, safeFilename);
            String fullPath = target.getAbsolutePath();
            
            // 4.保存文件内容
            log.info("准备保存文件：{}，资源大小：{} 字节", fullPath, resource.contentLength());
            
            // 使用Files.copy代替低级IO操作，确保文件内容正确复制
            java.nio.file.Files.copy(
                resource.getInputStream(),
                target.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING  // 如果文件已存在则替换
            );
            
            // 5.检查保存后的文件大小
            if (target.length() == 0) {
                log.error("文件保存后大小为0，可能未成功保存内容：{}", fullPath);
                return false;
            }
            
            log.info("文件保存成功：{}，大小：{} 字节", fullPath, target.length());
            
            // 6.保存映射关系 - 保存完整路径
            chatFiles.put(chatId, fullPath);
            log.info("保存chatId与文件的映射关系：{} -> {}", chatId, fullPath);
            
            return true;
        } catch (Exception e) {
            log.error("保存文件失败", e);
            return false;
        }
    }

    @Override
    public Resource getFileByChatId(String chatId)
    {
        if (chatId == null || chatId.isEmpty()) {
            log.error("获取文件失败：chatId为空");
            return null;
        }
        
        String filePath = chatFiles.getProperty(chatId);
        if (filePath == null || filePath.isEmpty()) {
            log.error("获取文件失败：chatId={}没有对应的文件", chatId);
            return null;
        }
        
        log.info("获取文件：chatId={}，filePath={}", chatId, filePath);
        
        // 使用绝对路径创建FileSystemResource
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            log.error("文件不存在或不是普通文件：{}", filePath);
            return null;
        }
        
        // 检查文件大小
        if (file.length() == 0) {
            log.warn("文件大小为0：{}", filePath);
        } else {
            log.info("文件大小：{} 字节", file.length());
        }
        
        return new FileSystemResource(file);
    }

    // TODO: 后续添加持久化逻辑
}
