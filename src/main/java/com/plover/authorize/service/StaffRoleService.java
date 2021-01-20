package com.plover.authorize.service;

import com.plover.authorize.model.StaffRole;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.service
 *
 * @author : xywei
 * @date : 2021-01-20
 */
public interface StaffRoleService {

    /**
     * 获取所有角色
     *
     * @return
     */
    List<StaffRole> list();

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    StaffRole findById(Integer id);

    /**
     * 新增
     *
     * @param staffRole
     * @return
     */
    int add(StaffRole staffRole);

    /**
     * 更新
     *
     * @param staffRole
     * @return
     */
    int update(StaffRole staffRole);

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    int delete(Integer id, String updateBy);
}
