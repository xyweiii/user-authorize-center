package com.plover.authorize.data;

import com.plover.authorize.enums.AuthorityType;
import com.plover.authorize.model.App;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.StaffRole;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 角色 对 app应用授权的资源 权限控制
 *
 * @author xywei
 */
@Data
@Document(collection = "role_app_resource_authority")
public class RoleAppResourceAuthorityData {

    /**
     * id
     */
    @Id
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
     * 对资源的权限集合
     *
     * @see AuthorityType#getCode()
     */
    List<Integer> authority;

    /**
     * 创建时间
     */
    private Date createDate = new Date();

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateDate = new Date();

    /**
     * 更新人
     */
    private String updateBy;
}