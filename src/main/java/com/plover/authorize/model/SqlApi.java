package com.plover.authorize.model;

import com.plover.authorize.common.BaseModel;
import lombok.Data;

/**
 * Project:p804-sql-api
 * Package: com.x04.sql.model
 *
 * @author : xywei
 * @date : 2021-03-01
 */
@Data
public class SqlApi extends BaseModel {

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
