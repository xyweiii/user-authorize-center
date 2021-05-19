package com.plover.authorize.service;

import com.plover.authorize.form.StaffQueryForm;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.StaffRole;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.service
 *
 * @author : xywei
 * @date : 2021-01-14
 */
public interface StaffService {

    /**
     * list
     *
     * @param queryForm
     * @return
     */
    List<Staff> list(StaffQueryForm queryForm);

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    int count(StaffQueryForm queryForm);

    /**
     * 根据id 查找
     *
     * @param id
     * @return
     */
    Staff findById(Integer id);

    /**
     * 根据userName进行查找
     *
     * @param userName
     * @return
     */
    Staff findByUserName(String userName);

    /**
     * 根据 psnCode 查询
     *
     * @param psnCode
     * @return
     */
    Staff findByPsnCode(String psnCode);

    /**
     * 根据某个角色查询
     *
     * @param roleId 角色id
     * @return
     * @see StaffRole#getId()
     */
    List<Staff> findByRoleId(Integer roleId);

    /**
     * 新增
     *
     * @param staff 员工
     * @return
     */
    int add(Staff staff);

    /**
     * 更新
     *
     * @param staff
     * @return
     */
    int update(Staff staff);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(Integer id, String updateBy);
}
