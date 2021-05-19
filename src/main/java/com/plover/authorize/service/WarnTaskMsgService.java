package com.plover.authorize.service;

import com.plover.authorize.data.WarnTaskMsgData;
import com.plover.authorize.form.WarnTaskLogQueryForm;
import com.plover.authorize.form.WarnTaskMsgQueryForm;

import java.util.List;

public interface WarnTaskMsgService {

    /**
     * 列表查询
     *
     * @return
     */
    List<WarnTaskMsgData> list(WarnTaskMsgQueryForm queryForm);

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    int count(WarnTaskMsgQueryForm queryForm);

    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    WarnTaskMsgData findById(String id);

    /**
     * 新增
     *
     * @param warnTaskMsgData
     * @return
     */
    boolean add(WarnTaskMsgData warnTaskMsgData);

    /**
     * @param id
     * @param status 0:未读 1:已读
     * @return
     */
    boolean updateStatus(String id, int status);

}
