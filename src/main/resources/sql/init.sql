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


create table if not exists user_center.staff
(
    id             int auto_increment comment 'id'
        primary key,
    userName       varchar(45) default ''                null comment '用户登录名: 形如 yuanchengzong',
    email          varchar(45) default ''                null comment '邮箱',
    realName       varchar(45) default ''                null comment '真实姓名',
    idNumber       varchar(45) default ''                null comment '证件号: 基本上是身份证号',
    mobile         varchar(15) default ''                null comment '手机号码',
    sex            varchar(5)  default ''                null comment '性别',
    birthDay       varchar(15) default ''                null comment '出生日期',
    ntPlaceCode    varchar(10) default ''                null comment '户籍所在地编码',
    ntPlaceName    varchar(50) default ''                null comment '户籍所在地',
    birthPlaceCode varchar(10) default ''                null comment '出生地编码',
    birthPlaceName varchar(50) default ''                null comment '出生地',
    nation         varchar(15) default ''                null comment '民族',
    marriage       varchar(20) default ''                null comment '婚姻情况',
    permanreside   varchar(50) default ''                null comment '户口所在地',
    address        varchar(50) default ''                null comment '居住地址',
    technical      varchar(40) default ''                null comment '技术方向',
    status         int         default 0                 null comment '状态: 0:正常 1:禁用',
    deleted        int         default 0                 null comment '是否删除 0:未删 1:已删',
    createDate     datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    createBy       varchar(30) default 'system'          null comment '创建人',
    updateDate     datetime    default CURRENT_TIMESTAMP null,
    updateBy       varchar(30) default 'system'          null comment '更新人'
)
    comment '员工雇员库';


