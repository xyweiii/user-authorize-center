package com.plover.authorize.form;

import com.plover.authorize.common.QueryForm;
import com.plover.authorize.model.Staff;
import lombok.Data;

@Data
public class WarnTaskMsgQueryForm extends QueryForm {

    /**
     * 用户id
     *
     * @see Staff#getId()
     */
    private Integer staffId;

    /**
     * 状态 0:未读 1:已读
     */
    private Integer status;
}
