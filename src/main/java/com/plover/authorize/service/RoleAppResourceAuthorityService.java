package com.plover.authorize.service;

import com.plover.authorize.data.RoleAppResourceAuthorityData;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.service
 *
 * @author : xywei
 * @date : 2021-02-23
 */
public interface RoleAppResourceAuthorityService {

    /**
     * 根据 roleId,resourceId 查询
     */
    RoleAppResourceAuthorityData findByRoleIdAndResourceId(Integer roleId, Integer resourceId);

    /**
     * 新增
     *
     * @param authorityData 权限控制
     * @return
     */
    boolean add(RoleAppResourceAuthorityData authorityData);


    /**
     * 更新
     *
     * @param authorityData
     * @return
     */
    boolean update(RoleAppResourceAuthorityData authorityData);
}
