package com.plover.authorize.model;

import com.plover.authorize.common.BaseModel;
import lombok.Data;

/**
 * 雇员角色
 *
 * @author xywei
 */
@Data
public class StaffRole extends BaseModel {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除  0:未删除 1:已删除
     */
    private Integer deleted;

}