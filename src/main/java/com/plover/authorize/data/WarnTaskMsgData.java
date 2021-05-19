package com.plover.authorize.data;


import com.plover.authorize.model.App;
import com.plover.authorize.model.Staff;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 预警任务 执行日志
 */
@Data
@Document(collection = "warn_task_msg")
public class WarnTaskMsgData {

    @Id
    private String id;

    /**
     * 预警任务id
     */
    private String taskId;

    /**
     * 应用id
     *
     * @see App#getId()
     */
    private Integer appId;

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
    private Date createDate = new Date();

    /**
     * 更新时间
     */
    private Date updateDate = new Date();
}
