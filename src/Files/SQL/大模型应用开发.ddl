create table llm_chat_history_list
(
    chat_id             varchar(64)   primary key comment '会话id',
    chat_tittle      varchar(100) not null           comment '会话标题',
    chat_tag         varchar(40) not null           comment '会话标签',
    create_time  varchar(20) not null           comment '创建时间',
    update_time varchar(20) not null           comment '更新时间'
)
    comment '大模型会话历史列表';


create table llm_chat_history
(
    chat_id                varchar(64)   primary key comment '会话id',
    chat_role           varchar(20) not null           comment '会话角色（枚举）',
    chat_content   blob              comment '会话详情'
)
    comment '大模型会话历史详情表';

create table llm_functioncalling_programmer
(
    programmer_name varchar(64) primary key comment '编程者名称',
    education       varchar(5)     not null comment '程序员的学历  0-无 1-初中 2-高中 3-大专 4-本科 5-硕士 6-博士',
    programmerType  varchar(5)     not null comment '程序员类型    0-全栈 1-前端 2-后端 3-数据 4-测试 5-运维 6-其他',
    salary          varchar(64)    not null comment '程序员薪',
    experience      varchar(64)    not null comment '程序员经验（年）'
)
    comment '大模型智能客服-程序员表';