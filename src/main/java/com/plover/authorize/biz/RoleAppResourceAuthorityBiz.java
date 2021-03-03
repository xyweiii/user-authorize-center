package com.plover.authorize.biz;

import com.plover.authorize.data.RoleAppResourceAuthorityData;
import com.plover.authorize.entity.RoleAppResourceAuthorityEntity;
import com.plover.authorize.model.App;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.RoleAppResourceAuthorityService;
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
public class RoleAppResourceAuthorityBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private RoleAppResourceAuthorityService authorityService;

    /**
     * 根据 角色id 应用id查找
     *
     * @param roleId     角色id
     * @param resourceId 资源id
     * @return
     * @see StaffRole#getId()
     * @see App#getId()
     */
    public RoleAppResourceAuthorityEntity findByRoleIdAndResourceId(Integer roleId, Integer resourceId) {
        RoleAppResourceAuthorityData data = authorityService.findByRoleIdAndResourceId(roleId, resourceId);
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
    public RoleAppResourceAuthorityEntity convert(RoleAppResourceAuthorityData data) {
        RoleAppResourceAuthorityEntity entity = new RoleAppResourceAuthorityEntity();
        BeanUtils.copyProperties(data, entity);
        if (data.getCreateDate() != null) {
            entity.setCreateDate(DateFormatUtils.format(data.getCreateDate(), DATE_TIME_FORMAT));
        }
        if (data.getUpdateDate() != null) {
            entity.setUpdateDate(DateFormatUtils.format(data.getUpdateDate(), DATE_TIME_FORMAT));
        }
        return entity;
    }

    /**
     * 保存 authority
     *
     * @param authorityData
     * @return
     */
    public Boolean save(RoleAppResourceAuthorityData authorityData) {
        Integer roleId = authorityData.getRoleId();
        Integer resourceId = authorityData.getResourceId();
        RoleAppResourceAuthorityData existData = authorityService.findByRoleIdAndResourceId(roleId, resourceId);
        if (existData != null) {
            return authorityService.update(authorityData);
        } else {
            return authorityService.add(authorityData);
        }
    }
}
