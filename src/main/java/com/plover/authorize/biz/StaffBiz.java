package com.plover.authorize.biz;

import com.plover.authorize.common.PageList;
import com.plover.authorize.entity.StaffEntity;
import com.plover.authorize.form.StaffQueryForm;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.StaffRoleService;
import com.plover.authorize.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project:user-center
 * Package: com.plover.user.biz
 *
 * @author : xywei
 * @date : 2021-01-14
 */
@Component
@Slf4j
public class StaffBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffRoleService roleService;

    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    public PageList<StaffEntity> list(StaffQueryForm queryForm) {
        PageList<StaffEntity> page = new PageList<>();
        List<Staff> staffList = staffService.list(queryForm);
        List<StaffEntity> entityList = convert(staffList);
        int totalCount = staffService.count(queryForm);
        page.setPageNum(queryForm.pageNum);
        page.setPageSize(queryForm.pageSize);
        page.setTotal(totalCount);
        page.setDataList(entityList);
        return page;
    }

    /**
     * staff数据转换
     * staff -->  staffEntity
     *
     * @param staffList
     * @return
     */
    public List<StaffEntity> convert(List<Staff> staffList) {
        return staffList.stream().map(staff -> {
            StaffEntity staffEntity = new StaffEntity();
            BeanUtils.copyProperties(staff, staffEntity);
            if (staff.getCreateDate() != null) {
                staffEntity.setCreateDate(DateFormatUtils.format(staff.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (staff.getUpdateDate() != null) {
                staffEntity.setUpdateDate(DateFormatUtils.format(staff.getUpdateDate(), DATE_TIME_FORMAT));
            }
            String role = staff.getRole();
            if (StringUtils.isNotBlank(role)) {
                List<StaffRole> roleList = Arrays.stream(role.split(","))
                        .map(roleId -> roleService.findById(Integer.valueOf(roleId))).collect(Collectors.toList());
                staffEntity.setRoleList(roleList);
            }
            return staffEntity;
        }).collect(Collectors.toList());
    }

    /**
     * 新增
     *
     * @param staff
     * @return
     */
    public int add(Staff staff) {
        return staffService.add(staff);
    }

    /**
     * update
     *
     * @param staff
     * @return
     */
    public int update(Staff staff) {
        return staffService.update(staff);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    public int delete(Integer id, String updateBy) {
        return staffService.deleteById(id, updateBy);
    }
}
