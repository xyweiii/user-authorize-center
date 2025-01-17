package com.plover.authorize.form;

import com.plover.authorize.common.QueryForm;
import lombok.Data;

/**
 * 订单查询条件
 *
 * @author xywei
 */
@Data
public class UserQueryForm extends QueryForm {

    /**
     * 用户名
     */
    private String userName;
}
