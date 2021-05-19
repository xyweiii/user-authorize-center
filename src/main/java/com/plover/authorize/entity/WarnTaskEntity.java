package com.plover.authorize.entity;


import com.plover.authorize.model.App;
import com.plover.authorize.model.StaffRole;
import lombok.Data;

import java.util.List;

@Data
public class WarnTaskEntity {

    private String id;

    /**
     * 应用id
     *
     * @see App#getId()
     */
    private int appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 预警名称
     */
    private String name;

    /**
     * sql查询语句
     */
    private String sql;

    /**
     * 告警推送角色id
     *
     * @see StaffRole#getId()
     */
    private List<Integer> roleIds;

    /**
     * 角色列表
     */
    private List<StaffRole> roleList;

    /**
     * 推送模版
     */
    private String pushTemplate;

    /**
     * 紧急程度
     */
    private String warnLevel;

    /**
     * 描述
     */
    private String desc;

    /***
     *  推送日志数量
     */
    private int logCount;

    /**
     * 0:启用 1:停用
     */
    private int status;

    /**
     * 0:未删除 1:已删除
     */
    private int deleted;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 更新时间
     */
    private String updateDate;
}
