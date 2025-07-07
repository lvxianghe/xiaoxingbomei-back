-- 小型博美AI系统数据库初始化脚本
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `ysx-app` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `ysx-app`;

-- 大模型会话历史列表表
CREATE TABLE IF NOT EXISTS `llm_chat_history_list` (
    `chat_id`     varchar(64)                NOT NULL COMMENT '会话id' PRIMARY KEY,
    `chat_tittle` varchar(100)               NOT NULL COMMENT '会话标题',
    `chat_tag`    varchar(40)                NOT NULL COMMENT '会话标签',
    `create_time` varchar(50) DEFAULT '今天' NULL COMMENT '创建时间（支持中文描述）',
    `update_time` varchar(50) DEFAULT '今天' NULL COMMENT '更新时间（支持中文描述）'
) COMMENT '大模型会话历史列表';

-- 大模型会话历史详情表
CREATE TABLE IF NOT EXISTS `llm_chat_history` (
    `id`           bigint AUTO_INCREMENT COMMENT '自增主键' PRIMARY KEY,
    `chat_id`      varchar(64)                NOT NULL COMMENT '会话id',
    `chat_role`    varchar(20)                NOT NULL COMMENT '会话角色（user/assistant/system）',
    `chat_content` text                       NOT NULL COMMENT '会话内容',
    `create_time`  varchar(50) DEFAULT '刚刚' NULL COMMENT '创建时间（支持中文描述）',
    CONSTRAINT `llm_chat_history_ibfk_1`
        FOREIGN KEY (`chat_id`) REFERENCES `llm_chat_history_list` (`chat_id`)
            ON DELETE CASCADE
) COMMENT '大模型会话历史详情表';

-- 为会话历史表创建索引
CREATE INDEX `idx_chat_id` ON `llm_chat_history` (`chat_id`);
CREATE INDEX `idx_create_time` ON `llm_chat_history` (`create_time`);

-- 大模型资源表
CREATE TABLE IF NOT EXISTS `llm_model_resource` (
    `model_provider`       varchar(50)                         NOT NULL COMMENT '模型提供者（ollama/openai）',
    `model_name`           varchar(100)                        NOT NULL COMMENT '大模型名称（如 grok-3, deepseek-r1:14B）',
    `model_description`    varchar(500)                        NOT NULL COMMENT '大模型描述',
    `model_tag`            varchar(200)                        NOT NULL COMMENT '提示词标签（推理,创作,多模态）',
    `embedding_model_name` varchar(100)                        NULL COMMENT '嵌入模型名称（如 text-embedding-v3）',
    `embedding_dimensions` int                                 NULL COMMENT '嵌入模型维度（如 1024）',
    `create_time`          timestamp DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time`          timestamp DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`model_provider`, `model_name`)
) COMMENT '大模型-模型来源表';

-- 为模型资源表创建索引
CREATE INDEX `idx_create_time` ON `llm_model_resource` (`create_time`);
CREATE INDEX `idx_model_provider` ON `llm_model_resource` (`model_provider`);

-- 大模型系统提示词表
CREATE TABLE IF NOT EXISTS `llm_system_prompt` (
    `prompt_id`          varchar(64)   NOT NULL COMMENT '主键' PRIMARY KEY,
    `prompt_name`        varchar(40)   NOT NULL COMMENT '提示词名字',
    `prompt_type`        varchar(10)   NOT NULL COMMENT '提示词类型',
    `prompt_description` varchar(100)  NOT NULL COMMENT '提示词描述',
    `prompt_tag`         varchar(100)  NOT NULL COMMENT '提示词标签（A,B,C）',
    `prompt_content`     varchar(2000) NOT NULL COMMENT '提示词内容',
    `status`             varchar(5)    NOT NULL COMMENT '状态',
    `function_tool_id`   varchar(100)  NULL COMMENT '关联的工具ID，为空则不使用工具调用'
) COMMENT '大模型-系统提示词表';

-- 插入初始数据
INSERT INTO `llm_model_resource` (`model_provider`, `model_name`, `model_description`, `model_tag`, `embedding_model_name`, `embedding_dimensions`) VALUES
('openai', 'gpt-4o', 'OpenAI GPT-4o是最新的多模态大语言模型，支持文本、图像和音频处理', '多模态,文本生成,图像理解,音频处理', 'text-embedding-v3', 1024),
('openai', 'grok-3', 'Grok-3是xAI开发的最新一代大语言模型，具有强大的推理和创作能力', '推理,创作,多模态', 'text-embedding-v3', 1024),
('ollama', 'deepseek-r1:14B', 'DeepSeek-R1是一个专注于推理的大语言模型，在数学和逻辑推理方面表现优异', '推理,数学,逻辑,编程', NULL, NULL),
('ollama', 'qwen3:14b', '通义千问3是阿里云开发的大语言模型，在中文理解和生成方面表现出色', '中文,对话,文本生成,知识问答', NULL, NULL);

-- 创建默认管理员用户表（可选）
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          bigint AUTO_INCREMENT PRIMARY KEY,
    `username`    varchar(50) NOT NULL UNIQUE COMMENT '用户名',
    `password`    varchar(100) NOT NULL COMMENT '密码（加密）',
    `nickname`    varchar(50) DEFAULT NULL COMMENT '昵称',
    `email`       varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone`       varchar(20) DEFAULT NULL COMMENT '手机号',
    `avatar`      varchar(200) DEFAULT NULL COMMENT '头像URL',
    `status`      tinyint DEFAULT 1 COMMENT '状态（1:启用 0:禁用）',
    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '系统用户表';

-- 插入默认管理员用户
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iA6SBvZqk8wYCuGFmYZGqQMfKDrO', '系统管理员', 'admin@xiaoxingbomei.com', 1);
-- 默认密码: admin123 