package com.plover.authorize.service.impl;

import com.google.common.collect.Lists;
import com.plover.authorize.form.StaffQueryForm;
import com.plover.authorize.mapper.StaffMapper;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.StaffService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.service.impl
 *
 * @author : xywei
 * @date : 2021-01-14
 */
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffMapper staffMapper;

    /**
     * list
     *
     * @param queryForm
     * @return
     */
    @Override
    public List<Staff> list(StaffQueryForm queryForm) {
        return staffMapper.list(queryForm);
    }

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    @Override
    public int count(StaffQueryForm queryForm) {
        return staffMapper.count(queryForm);
    }

    /**
     * 根据id 查找
     *
     * @param id
     * @return
     */
    @Override
    public Staff findById(Integer id) {
        if (id == null) {
            return null;
        }
        return staffMapper.findById(id);
    }

    /**
     * 根据userName进行查找
     *
     * @param userName
     * @return
     */
    @Override
    public Staff findByUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        return staffMapper.findByUserName(userName);
    }


    /**
     * 根据 psnCode 查询
     *
     * @param psnCode
     * @return
     */
    @Override
    public Staff findByPsnCode(String psnCode) {
        if (StringUtils.isBlank(psnCode)) {
            return null;
        }
        return staffMapper.findByPsnCode(psnCode);
    }


    /**
     * 根据某个角色查询
     *
     * @param roleId 角色id
     * @return
     * @see StaffRole#getId()
     */
    @Override
    public List<Staff> findByRoleId(Integer roleId) {
        if (roleId == null) {
            return Lists.newArrayList();
        }
        return staffMapper.findByRoleId(roleId);
    }

    /**
     * 新增
     *
     * @param staff 员工
     * @return
     */
    @Override
    public int add(Staff staff) {
        return staffMapper.add(staff);
    }

    /**
     * 更新
     *
     * @param staff
     * @return
     */
    @Override
    public int update(Staff staff) {
        return staffMapper.update(staff);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    @Override
    public int deleteById(Integer id, String updateBy) {
        if (id == null) {
            return 0;
        }
        return staffMapper.deleteById(id, updateBy);
    }
}
