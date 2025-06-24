package org.xiaoxingbomei.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoxingbomei.common.entity.response.GlobalResponse;

import java.util.List;

/**
 * 作为RAG等 文件service
 */
public interface FileService
{

    GlobalResponse uploadFile(String chatId, MultipartFile file);

    org.springframework.http.ResponseEntity<Resource> downloadFile(String paramString);


    /**
     * 保存文件,还要记录chatId与文件的映射关系
     * @param chatId 会话id
     * @param resource 文件
     * @return 上传成功，返回true； 否则返回false
     */
    boolean saveFileWithChatId(String chatId, Resource resource);

    /**
     * 根据chatId获取文件
     * @param chatId 会话id
     * @return 找到的文件
     */
    Resource getFileByChatId(String chatId);

    /**
     * 根据chatId和查询内容进行文档内容搜索
     * @param chatId 会话ID
     * @param query 查询内容
     * @param maxResults 最大返回结果数
     * @return 相关文档片段列表
     */
    List<String> searchDocumentContent(String chatId, String query, int maxResults);
}
