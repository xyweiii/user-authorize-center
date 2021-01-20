package com.plover.authorize.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xywei
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 7068649348987203064L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private String updateDate;

    /**
     * 更新人
     */
    private String updateBy;
}
