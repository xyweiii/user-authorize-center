package com.plover.authorize.biz;

import com.plover.authorize.common.PageList;
import com.plover.authorize.data.WarnTaskData;
import com.plover.authorize.data.WarnTaskLogData;
import com.plover.authorize.entity.WarnTaskLogEntity;
import com.plover.authorize.form.WarnTaskLogQueryForm;
import com.plover.authorize.model.App;
import com.plover.authorize.model.Staff;
import com.plover.authorize.service.AppService;
import com.plover.authorize.service.StaffService;
import com.plover.authorize.service.WarnTaskLogService;
import com.plover.authorize.service.WarnTaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarnTaskLogBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private WarnTaskService taskService;

    @Autowired
    private WarnTaskLogService taskLogService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private AppService appService;

    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    public PageList<WarnTaskLogEntity> list(WarnTaskLogQueryForm queryForm) {
        PageList<WarnTaskLogEntity> page = new PageList<>();
        List<WarnTaskLogData> dataList = taskLogService.list(queryForm);
        List<WarnTaskLogEntity> entityList = convertList(dataList);
        int totalCount = taskLogService.count(queryForm);
        page.setPageNum(queryForm.pageNum);
        page.setPageSize(queryForm.pageSize);
        page.setTotal(totalCount);
        page.setDataList(entityList);
        return page;
    }

    /**
     * data -> entity
     *
     * @param dataList
     * @return
     */
    public List<WarnTaskLogEntity> convertList(List<WarnTaskLogData> dataList) {
        return dataList.stream().map(data -> {
                    WarnTaskLogEntity entity = new WarnTaskLogEntity();
                    BeanUtils.copyProperties(data, entity);
                    String taskId = data.getTaskId();
                    if (StringUtils.isNotBlank(taskId)) {
                        WarnTaskData task = taskService.findById(taskId);
                        if (task != null) {
                            entity.setTaskName(task.getName());
                            entity.setWarnLevel(task.getWarnLevel());
                            Integer appId = task.getAppId();
                            App app = appService.findById(appId);
                            if (app != null) {
                                entity.setAppName(app.getAppName());
                            }
                        }
                    }
                    List<Integer> staffIds = entity.getStaffIds();
                    if (CollectionUtils.isNotEmpty(staffIds)) {
                        List<String> staffNameList = entity.getStaffIds().stream().map(staffId -> {
                            Staff staff = staffService.findById(staffId);
                            return staff.getRealName();
                        }).collect(Collectors.toList());
                        entity.setStaffNameList(staffNameList);
                    }
                    if (data.getCreateDate() != null) {
                        entity.setCreateDate(DateFormatUtils.format(data.getCreateDate(), DATE_TIME_FORMAT));
                    }
                    if (data.getUpdateDate() != null) {
                        entity.setUpdateDate(DateFormatUtils.format(data.getCreateDate(), DATE_TIME_FORMAT));
                    }
                    return entity;
                }
        ).collect(Collectors.toList());
    }
}
