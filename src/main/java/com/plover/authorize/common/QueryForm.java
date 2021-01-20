package com.plover.authorize.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xywei
 * <p>
 * 分页查询 基本条件
 */
@Data
public class QueryForm implements Serializable {

    private static final long serialVersionUID = -8758986802171008803L;

    public Integer pageNum = 1;
    public Integer pageSize = 20;

    public Integer start;
    public Integer limit;

    public Integer getStart() {
        return (pageNum - 1) * pageSize;
    }

    public void setStart(Integer start) {
        this.start = (pageNum - 1) * pageSize;
    }

    public Integer getLimit() {
        return pageSize;
    }

    public void setLimit(Integer limit) {
        this.limit = pageSize;
    }
}
