package com.plover.user.form;

import com.plover.user.common.QueryForm;
import lombok.Data;

/**
 * 订单查询条件
 *
 * @author xywei
 */
@Data
public class StaffQueryForm extends QueryForm {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobile;
}