package com.plover.authorize.entity;

import com.plover.authorize.common.BaseEntity;
import lombok.Data;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.entity
 *
 * @author : xywei
 * @date : 2021-03-02
 */
@Data
public class SqlApiEntity extends BaseEntity {
    /**
     * 标题
     */
    private String title;

    /**
     * sql 查询语句
     */
    private String sqlStr;

    /**
     * 对应的api地址
     */
    private String apiUrl;
}
