package com.plover.authorize.entity;


import com.plover.authorize.data.WarnTaskData;
import com.plover.authorize.model.Staff;
import lombok.Data;

import java.util.List;

@Data
public class WarnTaskLogEntity {

    private String id;

    /**
     * 预警任务id
     */
    private String taskId;

    /**
     * 预警任务名称
     */
    private String taskName;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 紧急程度
     *
     * @see WarnTaskData#getWarnLevel()
     */
    private String warnLevel;

    /**
     * 推送用户id 集合
     *
     * @see Staff#getId()
     */
    private List<Integer> staffIds;

    /**
     * 推送用户名 集合
     *
     * @see Staff#getRealName()
     */
    private List<String> staffNameList;

    /**
     * 状态 0：成功 1:失败
     */
    private int status;

    /**
     * 原因
     */
    private String msg;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 更新时间
     */
    private String updateDate;
}
