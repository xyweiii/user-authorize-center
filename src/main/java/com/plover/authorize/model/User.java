package com.plover.authorize.model;

import com.plover.authorize.common.BaseModel;
import lombok.Data;
import lombok.ToString;

/**
 * 系统用户表
 */
@Data
@ToString
public class User extends BaseModel {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色
     */
    private String role;

    /**
     * email邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 状态
     * 0: 正常
     * 1: 禁用
     */
    private int status;

    /**
     * 删除
     * 0:未删除
     * 1:已删除
     */
    private int deleted;
}