package com.plover.authorize.biz;

import com.plover.authorize.common.PageList;
import com.plover.authorize.data.RoleAppResourceData;
import com.plover.authorize.entity.AppEntity;
import com.plover.authorize.entity.StaffEntity;
import com.plover.authorize.form.StaffQueryForm;
import com.plover.authorize.model.App;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    @Autowired
    private AppService appService;

    @Autowired
    private AppResourceService appResourceService;

    @Autowired
    private RoleAppResourceService roleAppResourceService;

    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    public PageList<StaffEntity> list(StaffQueryForm queryForm) {
        PageList<StaffEntity> page = new PageList<>();
        List<Staff> staffList = staffService.list(queryForm);
        List<StaffEntity> entityList = convertList(staffList);
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
    public List<StaffEntity> convertList(List<Staff> staffList) {
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
     * staff数据转换
     * staff -->  staffEntity
     *
     * @param staff
     * @return
     */
    public StaffEntity getRoleAppResourceInfo(Staff staff) {
        StaffEntity staffEntity = new StaffEntity();
        BeanUtils.copyProperties(staff, staffEntity);
        String role = staff.getRole();
        if (StringUtils.isNotBlank(role)) {
            List<StaffRole> roleList = Arrays.stream(role.split(","))
                    .map(roleId -> roleService.findById(Integer.valueOf(roleId))).collect(Collectors.toList());
            staffEntity.setRoleList(roleList);
        }
        List<App> appList = appService.list();
        //  获取该 权限在 该app 中的 访问资源集合
        List<AppEntity> appEntityList = appList.stream().map(app -> {
            AppEntity appEntity = new AppEntity();
            BeanUtils.copyProperties(app, appEntity);
            List<StaffRole> roleList = staffEntity.getRoleList();
            List<AppResource> appResourceList = new ArrayList<>();
            roleList.stream().forEach(staffRole -> {
                List<AppResource> appResources = getAppResourceByAppIdAndRoleId(staffRole.getId(), app.getId());
                if (!CollectionUtils.isEmpty(appResources)) {
                    appResourceList.addAll(appResources);
                }
            });
            appEntity.setResourceList(appResourceList);
            return appEntity;
        }).collect(Collectors.toList());
        staffEntity.setAppList(appEntityList);
        return staffEntity;
    }


    /**
     * 根据 appId 和 roleId 获取 访问资源集合
     *
     * @param roleId
     * @param appId
     * @return
     */
    public List<AppResource> getAppResourceByAppIdAndRoleId(Integer roleId, Integer appId) {
        RoleAppResourceData data = roleAppResourceService.findByRoleIdAndAppId(roleId, appId);
        if (data == null || CollectionUtils.isEmpty(data.getResources())) {
            return new ArrayList<>();
        }
        return data.getResources().stream()
                .map(id -> appResourceService.findById(id))
                .collect(Collectors.toList());
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
