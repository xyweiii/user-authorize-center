package com.plover.authorize.biz;

import com.plover.authorize.data.WarnTaskData;
import com.plover.authorize.entity.WarnTaskEntity;
import com.plover.authorize.model.App;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.AppService;
import com.plover.authorize.service.StaffRoleService;
import com.plover.authorize.service.WarnTaskLogService;
import com.plover.authorize.service.WarnTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WarnTaskBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private AppService appService;

    @Autowired
    private StaffRoleService roleService;

    @Autowired
    private WarnTaskService warnTaskService;

    @Autowired
    private WarnTaskLogService taskLogService;

    /**
     * 列表查询
     *
     * @return
     */
    public List<WarnTaskEntity> list() {
        List<WarnTaskData> dataList = warnTaskService.list();
        return convertList(dataList);
    }

    /**
     * data -> entity
     *
     * @param dataList
     * @return
     */
    public List<WarnTaskEntity> convertList(List<WarnTaskData> dataList) {
        return dataList.stream().map(data -> {
            WarnTaskEntity entity = new WarnTaskEntity();
            BeanUtils.copyProperties(data, entity);
            Integer appId = data.getAppId();
            App app = appService.findById(appId);
            if (app != null) {
                entity.setAppName(app.getAppName());
            }
            // logCount
            int logCount = taskLogService.countByTaskId(data.getId());
            entity.setLogCount(logCount);
            if (CollectionUtils.isNotEmpty(data.getRoleIds())) {
                List<StaffRole> roleList = data.getRoleIds().stream().map(roleId -> roleService.findById(roleId)).collect(Collectors.toList());
                entity.setRoleList(roleList);
            }

            if (data.getCreateDate() != null) {
                entity.setCreateDate(DateFormatUtils.format(data.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (data.getUpdateDate() != null) {
                entity.setUpdateDate(DateFormatUtils.format(data.getCreateDate(), DATE_TIME_FORMAT));
            }
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 新增
     *
     * @param warnTaskData
     * @return
     */
    public boolean add(WarnTaskData warnTaskData) {
        return warnTaskService.add(warnTaskData);
    }

    /**
     * 更新
     *
     * @param warnTaskData
     * @return
     */
    public boolean update(WarnTaskData warnTaskData) {
        return warnTaskService.update(warnTaskData);
    }

    /**
     * 更新任务状态
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateStatus(String id, int status) {
        return warnTaskService.updateStatus(id, status);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public boolean delete(String id) {
        return warnTaskService.delete(id);
    }
}
