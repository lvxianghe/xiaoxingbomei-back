package org.xiaoxingbomei.service;

import org.springframework.core.io.Resource;

/**
 * 作为RAG等 文件service
 */
public interface FileService
{
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
}
