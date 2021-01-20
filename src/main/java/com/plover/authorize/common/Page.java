package com.plover.authorize.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xywei
 */
@Data
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 7068649348987203064L;

    /**
     * 分页条数
     */
    private int pageSize;
    /**
     * 当前页数
     */
    private int pageNum;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 数据总数
     */
    private int totalCount;

    /**
     * 数据列表
     */
    private List<T> dataList;
}
