package com.plover.authorize.service;

import com.plover.authorize.data.WarnTaskLogData;
import com.plover.authorize.form.WarnTaskLogQueryForm;

import java.util.List;

public interface WarnTaskLogService {

    /**
     * 列表查询
     *
     * @return
     */
    List<WarnTaskLogData> list(WarnTaskLogQueryForm queryForm);

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    int count(WarnTaskLogQueryForm queryForm);

    /***
     * 根据 任务Id count
     * @param taskId
     * @return
     */
    int countByTaskId(String taskId);

    /**
     * 增加
     *
     * @param taskLogData
     * @return
     */
    boolean add(WarnTaskLogData taskLogData);
}
