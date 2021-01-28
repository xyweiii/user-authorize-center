package com.plover.authorize.entity;

import com.plover.authorize.common.BaseEntity;
import com.plover.authorize.model.StaffRole;
import lombok.Data;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.entity
 *
 * @author : xywei
 * @date : 2021-01-19
 */
@Data
public class AppEntity extends BaseEntity {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用地址
     */
    private String appUrl;

    /**
     * 应用唯一标识
     */
    private String appId;

    /**
     * 描述
     */
    private String description;

    /**
     * app 授权哪些角色
     * 角色id,逗号隔开
     */
    private String role;

    /**
     * 删除
     * 0:未删除
     * 1:已删除
     */
    private int deleted;

    /**
     * 角色
     */
    private List<StaffRole> roleList;
}
