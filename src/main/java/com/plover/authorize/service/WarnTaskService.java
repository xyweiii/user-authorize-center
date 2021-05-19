package com.plover.authorize.service;

import com.plover.authorize.data.WarnTaskData;

import java.util.List;

public interface WarnTaskService {

    /**
     * 列表查询
     *
     * @return
     */
    List<WarnTaskData> list();


    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    WarnTaskData findById(String id);

    /**
     * 新增
     *
     * @param warnTask
     * @return
     */
    boolean add(WarnTaskData warnTask);

    /**
     * 更新
     *
     * @param warnTask
     * @return
     */
    boolean update(WarnTaskData warnTask);

    /**
     * 停用
     *
     * @param id
     * @return
     */
    boolean updateStatus(String id, int status);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(String id);
}
