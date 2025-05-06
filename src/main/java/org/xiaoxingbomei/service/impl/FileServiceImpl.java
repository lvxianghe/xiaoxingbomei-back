package org.xiaoxingbomei.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.xiaoxingbomei.service.FileService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Component
public class FileServiceImpl implements FileService
{

    @Autowired
    private  VectorStore vectorStore;

    // 会话id 与 文件名的对应关系，方便查询会话历史时重新加载文件
    private final Properties chatFiles = new Properties();  // properties 可以看做是 hashmap  支持持久化


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
            
            // 3.构建目标文件的完整路径
            File target = new File(saveDir, filename);
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

//    @PostConstruct
//    private void init()
//    {
//        FileSystemResource pdfResource = new FileSystemResource("src/Files/BAK/chat-pdf.properties");
//        if (pdfResource.exists())
//        {
//            try
//            {
//                chatFiles.load(new BufferedReader(new InputStreamReader(pdfResource.getInputStream(), StandardCharsets.UTF_8)));
//            } catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//        }
//        FileSystemResource vectorResource = new FileSystemResource("./src/Files/BAK/chat-pdf.json");
//        if (vectorResource.exists())
//        {
//            SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
//            simpleVectorStore.load(vectorResource);
//        }
//    }
//
//    @PreDestroy
//    private void persistent() {
//        try {
//            chatFiles.store(new FileWriter("./src/Files/BAK/chat-pdf.properties"), LocalDateTime.now().toString());
//            SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
//            simpleVectorStore.save(new File("./src/Files/BAK/chat-pdf.json"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
