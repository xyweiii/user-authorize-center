package com.plover.authorize.service.impl;

import com.plover.authorize.mapper.StaffRoleMapper;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.StaffRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.service.impl
 *
 * @author : xywei
 * @date : 2021-01-20
 */
@Service
public class StaffRoleServiceImpl implements StaffRoleService {

    @Autowired
    private StaffRoleMapper staffRoleMapper;

    /**
     * 获取所有角色
     *
     * @return
     */
    @Override
    public List<StaffRole> list() {
        return staffRoleMapper.list();
    }

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    @Override
    public StaffRole findById(Integer id) {
        if (id == null) {
            return null;
        }
        return staffRoleMapper.findById(id);
    }

    /**
     * 新增
     *
     * @param staffRole
     * @return
     */
    @Override
    public int add(StaffRole staffRole) {
        return staffRoleMapper.add(staffRole);
    }

    /**
     * 更新
     *
     * @param staffRole
     * @return
     */
    @Override
    public int update(StaffRole staffRole) {
        return staffRoleMapper.update(staffRole);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    @Override
    public int delete(Integer id, String updateBy) {
        if (id == null) {
            return 0;
        }
        return staffRoleMapper.deleteById(id, updateBy);
    }
}
