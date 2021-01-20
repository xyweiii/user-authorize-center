package com.plover.authorize.entity;

import com.plover.authorize.common.BaseEntity;
import lombok.Data;

/**
 * Project:user-center
 * Package: com.plover.user.entity
 *
 * @author : xywei
 * @date : 2021-01-20
 */
@Data
public class StaffRoleEntity extends BaseEntity {

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
