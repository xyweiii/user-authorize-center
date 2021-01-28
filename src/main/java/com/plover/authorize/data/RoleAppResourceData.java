package com.plover.authorize.data;

import com.plover.authorize.model.App;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.StaffRole;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * StaffRole 角色 与 AppResource 应用资源的 绑定关系  1:N
 *
 * @author xywei
 */
@Data
@Document(collection = "role_app_resource")
public class RoleAppResourceData {

    /**
     * id
     */
    @Id
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