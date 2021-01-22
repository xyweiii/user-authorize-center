package com.plover.authorize.biz;

import com.plover.authorize.entity.StaffRoleEntity;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.StaffRoleService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project:user-center
 * Package: com.plover.user.biz
 *
 * @author : xywei
 * @date : 2021-01-20
 */
@Component
public class StaffRoleBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private StaffRoleService roleService;

    /**
     * 获取所有 StaffRole
     *
     * @return
     */
    public List<StaffRoleEntity> list() {
        List<StaffRole> staffRoleList = roleService.list();
        return convert(staffRoleList);
    }

    /**
     * staffRole 数据转换
     * staffRole -->  staffRoleEntity
     *
     * @param staffRoleList
     * @return
     */
    public List<StaffRoleEntity> convert(List<StaffRole> staffRoleList) {
        return staffRoleList.stream().map(staff -> {
            StaffRoleEntity roleEntity = new StaffRoleEntity();
            BeanUtils.copyProperties(staff, roleEntity);
            if (staff.getCreateDate() != null) {
                roleEntity.setCreateDate(DateFormatUtils.format(staff.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (staff.getUpdateDate() != null) {
                roleEntity.setUpdateDate(DateFormatUtils.format(staff.getUpdateDate(), DATE_TIME_FORMAT));
            }
            return roleEntity;
        }).collect(Collectors.toList());
    }

    /**
     * 新增
     *
     * @param staffRole
     * @return
     */
    public int add(StaffRole staffRole) {
        return roleService.add(staffRole);
    }

    /**
     * 更新
     *
     * @param staffRole
     * @return
     */
    public int update(StaffRole staffRole) {
        return roleService.update(staffRole);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    public int delete(Integer id, String updateBy) {
        return roleService.delete(id, updateBy);
    }
}
