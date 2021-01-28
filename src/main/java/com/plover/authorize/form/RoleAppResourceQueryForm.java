package com.plover.authorize.form;

import com.plover.authorize.common.QueryForm;
import lombok.Data;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.form
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@Data
public class RoleAppResourceQueryForm extends QueryForm {

    /**
     * 角色id
     */
    private Integer roleId;

    /***
     * 应用id
     */
    private Integer appId;
}
