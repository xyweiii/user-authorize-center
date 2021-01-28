package com.plover.authorize.service;


import com.plover.authorize.data.RoleAppResourceData;


/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.service
 *
 * @author : xywei
 * @date : 2021-01-26
 */
public interface RoleAppResourceService {


    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    RoleAppResourceData findById(String id);

    /**
     * 根据 roleId appId 查询
     *
     * @param roleId
     * @param appId
     * @return
     */
    RoleAppResourceData findByRoleIdAndAppId(Integer roleId, Integer appId);

    /**
     * 新增
     *
     * @param roleAppResource
     * @return
     */
    boolean add(RoleAppResourceData roleAppResource);

    /**
     * 更新
     *
     * @param roleAppResource
     * @return
     */
    boolean update(RoleAppResourceData roleAppResource);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean deleteById(String id);
}
