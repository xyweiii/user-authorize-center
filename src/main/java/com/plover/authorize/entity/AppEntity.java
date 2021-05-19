package com.plover.authorize.entity;

import com.plover.authorize.common.BaseEntity;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.StaffRole;
import lombok.Data;

import java.util.List;

/**
 * 预警任务执行日志
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
     * 0: 业务系统
     * 1: 轻应用
     */
    private int type;

    /**
     * 是否需要登陆
     * 0: 不需要
     * 1：需要
     */
    private int needLogin;

    /**
     * 是否需要鉴权
     * 0： 不需要
     * 1：需要
     */
    private int needAuthority;

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

    /**
     * app资源
     */
    private List<AppResource> resourceList;
}
