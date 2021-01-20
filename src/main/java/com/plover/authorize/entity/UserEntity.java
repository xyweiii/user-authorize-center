package com.plover.authorize.entity;

import com.plover.authorize.common.BaseEntity;
import lombok.Data;


/**
 * @author xywei
 */
@Data
public class UserEntity extends BaseEntity {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 联系号码
     */
    private String mobile;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     * 0:正常
     * 1:禁用
     */
    private int status;

    /**
     * 是否删除
     * 0: 否
     * 1: 是
     */
    private int deleted;
}