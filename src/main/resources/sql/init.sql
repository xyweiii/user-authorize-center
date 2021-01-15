create table if not exists `user_center`.user
(
    id         int auto_increment
        primary key,
    userName   varchar(45)                          null comment '用户名',
    password   varchar(45)                          null comment '密码',
    role       varchar(45)                          null comment '角色',
    email      varchar(45)                          null comment '邮箱',
    mobile     varchar(45)                          null comment '手机号码',
    status     tinyint(1) default 0                 null comment '状态: 0:正常,1:禁用',
    deleted    tinyint(1) default 0                 null comment '是否删除: 0: 否 1: 是',
    createDate datetime   default CURRENT_TIMESTAMP null,
    updateDate datetime   default CURRENT_TIMESTAMP null,
    createBy   varchar(45)                          null comment '创建人',
    updateBy   varchar(45)                          null comment '更新人'
);

