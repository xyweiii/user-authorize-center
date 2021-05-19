package com.plover.authorize.entity;

import com.plover.authorize.model.Staff;
import lombok.Data;

@Data
public class WarnTaskMsgEntity {

    private String id;

    /**
     * 预警任务id
     */
    private String taskId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 用户id
     *
     * @see Staff#getId()
     */
    private Integer staffId;

    /**
     * 用户 psnCode
     *
     * @see Staff#getPsnCode()
     */
    private String psnCode;

    /**
     * 内容
     */
    private String content;

    /**
     * 0:未读  1:已读
     */
    private int status;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 更新时间
     */
    private String updateDate;
}
