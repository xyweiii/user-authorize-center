package com.plover.authorize.biz;

import com.plover.authorize.data.RoleAppResourceData;
import com.plover.authorize.entity.RoleAppResourceEntity;
import com.plover.authorize.model.App;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.AppService;
import com.plover.authorize.service.RoleAppResourceService;
import com.plover.authorize.service.StaffRoleService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.biz
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@Component
public class RoleAppResourceBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private RoleAppResourceService roleAppResourceService;

    @Autowired
    private StaffRoleService roleService;

    @Autowired
    private AppService appService;

    /**
     * 根据 角色id 应用id查找
     *
     * @param roleId 角色id
     * @param appId  应用id
     * @return
     * @see StaffRole#getId()
     * @see App#getId()
     */
    public RoleAppResourceEntity findByRoleIdAndAppId(Integer roleId, Integer appId) {
        RoleAppResourceData data = roleAppResourceService.findByRoleIdAndAppId(roleId, appId);
        if (data == null) {
            return null;
        }
        return convert(data);
    }

    /**
     * 数据转换
     * data --> entity
     *
     * @param data
     * @return
     */
    public RoleAppResourceEntity convert(RoleAppResourceData data) {
        RoleAppResourceEntity entity = new RoleAppResourceEntity();
        BeanUtils.copyProperties(data, entity);
        if (data.getAppId() != null) {
            App app = appService.findById(data.getAppId());
            entity.setApp(app);
        }
        if (data.getRoleId() != null) {
            StaffRole role = roleService.findById(data.getRoleId());
            entity.setRole(role);
        }
        if (data.getCreateDate() != null) {
            entity.setCreateDate(DateFormatUtils.format(data.getCreateDate(), DATE_TIME_FORMAT));
        }
        if (data.getUpdateDate() != null) {
            entity.setUpdateDate(DateFormatUtils.format(data.getUpdateDate(), DATE_TIME_FORMAT));
        }
        return entity;
    }

    /**
     * 保存 角色-资源 绑定关系
     *
     * @param roleAppResourceData
     * @return
     */
    public Boolean save(RoleAppResourceData roleAppResourceData) {
        Integer roleId = roleAppResourceData.getRoleId();
        Integer appId = roleAppResourceData.getAppId();
        RoleAppResourceData existData = roleAppResourceService.findByRoleIdAndAppId(roleId, appId);
        if (existData != null) {
            return roleAppResourceService.update(roleAppResourceData);
        } else {
            return roleAppResourceService.add(roleAppResourceData);
        }
    }
}
