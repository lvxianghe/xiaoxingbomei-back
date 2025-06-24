-- auto-generated definition
create table llm_chat_history
(
    id           bigint auto_increment comment '自增主键'
        primary key,
    chat_id      varchar(64)                not null comment '会话id',
    chat_role    varchar(20)                not null comment '会话角色（user/assistant/system）',
    chat_content text                       not null comment '会话内容',
    create_time  varchar(50) default '刚刚' null comment '创建时间（支持中文描述）',
    constraint llm_chat_history_ibfk_1
        foreign key (chat_id) references llm_chat_history_list (chat_id)
            on delete cascade
)
    comment '大模型会话历史详情表';

create index idx_chat_id
    on llm_chat_history (chat_id);

create index idx_create_time
    on llm_chat_history (create_time);

-- auto-generated definition
create table llm_chat_history_list
(
    chat_id     varchar(64)                not null comment '会话id'
        primary key,
    chat_tittle varchar(100)               not null comment '会话标题',
    chat_tag    varchar(40)                not null comment '会话标签',
    create_time varchar(50) default '今天' null comment '创建时间（支持中文描述）',
    update_time varchar(50) default '今天' null comment '更新时间（支持中文描述）'
)
    comment '大模型会话历史列表';

-- auto-generated definition
create table llm_model_resource
(
    model_provider       varchar(50)                         not null comment '模型提供者（ollama/openai）',
    model_name           varchar(100)                        not null comment '大模型名称（如 grok-3, deepseek-r1:14B）',
    model_description    varchar(500)                        not null comment '大模型描述',
    model_tag            varchar(200)                        not null comment '提示词标签（推理,创作,多模态）',
    embedding_model_name varchar(100)                        null comment '嵌入模型名称（如 text-embedding-v3）',
    embedding_dimensions int                                 null comment '嵌入模型维度（如 1024）',
    create_time          timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time          timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    primary key (model_provider, model_name)
)
    comment '大模型-模型来源表';

create index idx_create_time
    on llm_model_resource (create_time);

create index idx_model_provider
    on llm_model_resource (model_provider);

-- auto-generated definition
create table llm_system_prompt
(
    prompt_id          varchar(64)   not null comment '主键'
        primary key,
    prompt_name        varchar(40)   not null comment '主键ID',
    prompt_type        varchar(10)   not null comment '提示词名字',
    prompt_description varchar(100)  not null comment '提示词类型',
    prompt_tag         varchar(100)  not null comment '提示词描述',
    prompt_content     varchar(2000) not null comment '提示词标签（A,B,C）',
    status             varchar(5)    not null comment '提示词内容',
    function_tool_id   varchar(100)  null comment '关联的工具ID，为空则不使用工具调用'
)
    comment '大模型-系统提示词表';

