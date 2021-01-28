package com.plover.authorize.biz;

import com.plover.authorize.entity.AppEntity;
import com.plover.authorize.model.App;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.AppService;
import com.plover.authorize.service.StaffRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Project:user-center
 * Package: com.plover.user.biz
 *
 * @author : xywei
 * @date : 2021-01-19
 */
@Component
@Slf4j
public class AppBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private AppService appService;

    @Autowired
    private StaffRoleService roleService;

    /**
     * 根据Staff 获取其被授权的App应用
     * (1) app 如果没有设置授权 ,用户可以访问
     * (2) staff 如果没有设置授权 ,用户可以访问所有应用
     * (3) staff & app 都设置了 授权角色,规则: 只要 staff角色包含app授权角色中任意一项就可以访问该app
     *
     * @param staff
     * @return
     * @see App#getRole()
     * @see Staff#getRole()
     */
    public List<App> findAuthorizedAppByStaff(Staff staff) {
        List<App> appList = appService.list();
        String staffRole = staff.getRole();
        //满足规则 (2)
        if (StringUtils.isBlank(staffRole)) {
            return appList;
        }
        List<String> staffRoleIdList = Arrays.asList(staffRole.split(","));
        List<App> filterAppList = appList.stream().filter(app -> {
            try {
                String appRole = app.getRole();
                //满足规则 (1)
                if (StringUtils.isBlank(appRole)) {
                    return true;
                }
                //满足规则 (3)
                AtomicReference<Boolean> containsFlag = new AtomicReference<>(false);
                List<String> appRoleIdList = Arrays.asList(appRole.split(","));
                appRoleIdList.stream().forEach(roleId -> {
                    if (staffRoleIdList.contains(roleId)) {
                        containsFlag.set(true);
                    }
                });
                return containsFlag.get();
            } catch (Exception e) {
                log.error("occur error app:{}", app, e);
                return false;
            }
        }).collect(Collectors.toList());
        return filterAppList;
    }

    /**
     * 获取所有应用列表
     *
     * @return
     */
    public List<AppEntity> list() {
        List<App> appList = appService.list();
        return convert(appList);
    }

    /**
     * app数据转换
     * app -->  appEntity
     *
     * @param appList
     * @return
     */
    public List<AppEntity> convert(List<App> appList) {
        return appList.stream().map(app -> {
            AppEntity appEntity = new AppEntity();
            BeanUtils.copyProperties(app, appEntity);
            if (app.getCreateDate() != null) {
                appEntity.setCreateDate(DateFormatUtils.format(app.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (app.getUpdateDate() != null) {
                appEntity.setUpdateDate(DateFormatUtils.format(app.getUpdateDate(), DATE_TIME_FORMAT));
            }
            String role = app.getRole();
            if (StringUtils.isNotBlank(role)) {
                List<StaffRole> roleList = Arrays.stream(role.split(","))
                        .map(roleId -> roleService.findById(Integer.valueOf(roleId))).collect(Collectors.toList());
                appEntity.setRoleList(roleList);
            }
            return appEntity;
        }).collect(Collectors.toList());
    }

    /**
     * 新增
     *
     * @param app
     * @return
     */
    public int add(App app) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        app.setAppId(uuid);
        return appService.add(app);
    }

    /**
     * 更新
     *
     * @param app
     * @return
     */
    public int update(App app) {
        return appService.update(app);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy 更新人
     * @return
     */
    public int delete(Integer id, String updateBy) {
        if (id == null) {
            return 0;
        }
        return appService.delete(id, updateBy);
    }
}
