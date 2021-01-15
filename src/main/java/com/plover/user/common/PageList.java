package com.plover.user.common;

/**
 * Project:sioo-common
 * File: com.sioo.cloud.common.core.model
 *
 * @author : xywei
 * @date : 2018-12-21
 * Copyright 2006-2018 Sioo Co., Ltd. All rights reserved.
 */

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 查询结果List,适用于分页，或者需要知道总记录数
 *
 * @param <E>
 * @author dean
 */
@Data
public class PageList<E> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 页数
     */
    private int pageNum;

    /**
     * 数据总数
     */
    private int total = 0;

    /**
     * 结果
     */
    private List<E> dataList = Collections.emptyList();

    public PageList() {
    }

    public PageList(int pageSize, int pageNum, int total, List<E> dataList) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.total = total;
        this.dataList = dataList;
    }
}
