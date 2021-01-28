package com.plover.authorize.entity;

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
public class RoleAppResourceEntity {

    private String id;

    /**
     * 角色id
     *
     * @see StaffRole#getId()
     */
    private Integer roleId;

    /**
     * app应用Id
     *
     * @see App#getId()
     */
    private Integer appId;

    /**
     * 资源集合
     *
     * @see AppResource#getId()
     */
    List<Integer> resources;

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

    /**
     * 角色
     */
    private StaffRole role;

    /**
     * 应用
     */
    private App app;


}
