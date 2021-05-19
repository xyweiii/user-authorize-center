package com.plover.authorize.data;


import com.plover.authorize.model.App;
import com.plover.authorize.model.StaffRole;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


/**
 * 预警任务
 */
@Data
@Document(collection = "warn_task")
public class WarnTaskData {

    @Id
    private String id;

    /**
     * 应用id
     *
     * @see App#getId()
     */
    private int appId;

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
    private Date createDate = new Date();

    /**
     * 更新时间
     */
    private Date updateDate = new Date();
}
