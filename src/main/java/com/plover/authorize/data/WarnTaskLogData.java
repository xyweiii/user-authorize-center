package com.plover.authorize.data;


import com.plover.authorize.model.Staff;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 预警任务 执行日志
 */
@Data
@Document(collection = "warn_task_log")
public class WarnTaskLogData {

    @Id
    private String id;

    /**
     * 预警任务id
     */
    private String taskId;

    /**
     * 推送用户id 集合
     *
     * @see Staff#getId()
     */
    private List<Integer> staffIds;

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
    private Date createDate = new Date();

    /**
     * 更新时间
     */
    private Date updateDate = new Date();
}
