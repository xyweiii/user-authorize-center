package com.plover.authorize.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xywei
 */
@Data
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 7068649348987203064L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 更新人
     */
    private String updateBy;
}
