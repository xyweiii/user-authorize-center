package com.plover.authorize.form;

import com.plover.authorize.common.QueryForm;
import com.plover.authorize.data.WarnTaskData;
import lombok.Data;

@Data
public class WarnTaskLogQueryForm extends QueryForm {


    /**
     * 任务id
     *
     * @see WarnTaskData#getId()
     */
    private String taskId;

    /**
     * 状态 0:成功 1:失败
     */
    private Integer status;
}
