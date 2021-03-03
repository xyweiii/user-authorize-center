package com.plover.authorize.entity;

import com.plover.authorize.enums.AuthorityType;
import com.plover.authorize.model.App;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.StaffRole;
import lombok.Data;

import java.util.List;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.entity
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@Data
public class RoleAppResourceAuthorityEntity {

    private String id;

    /**
     * app应用Id
     *
     * @see App#getId()
     */
    private Integer appId;


    /**
     * 资源id
     *
     * @see AppResource#getId()
     */
    private Integer resourceId;

    /**
     * 角色id
     *
     * @see StaffRole#getId()
     */
    private Integer roleId;

    /**
     * 权限集合
     *
     * @see AuthorityType#getCode()
     */
    List<Integer> authority;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private String updateDate;

    /**
     * 更新人
     */
    private String updateBy;

}
