package com.plover.authorize.biz;

import com.plover.authorize.common.PageList;
import com.plover.authorize.data.WarnTaskMsgData;
import com.plover.authorize.entity.WarnTaskMsgEntity;
import com.plover.authorize.form.WarnTaskMsgQueryForm;
import com.plover.authorize.model.App;
import com.plover.authorize.service.AppService;
import com.plover.authorize.service.WarnTaskMsgService;
import com.plover.authorize.service.WarnTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WarnTaskMsgBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private AppService appService;

    @Autowired
    private WarnTaskMsgService taskMsgService;


    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    public PageList<WarnTaskMsgEntity> list(WarnTaskMsgQueryForm queryForm) {
        PageList<WarnTaskMsgEntity> page = new PageList<>();
        List<WarnTaskMsgData> dataList = taskMsgService.list(queryForm);
        List<WarnTaskMsgEntity> entityList = convertList(dataList);
        int totalCount = taskMsgService.count(queryForm);
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
    public List<WarnTaskMsgEntity> convertList(List<WarnTaskMsgData> dataList) {
        return dataList.stream().map(data -> {
            WarnTaskMsgEntity entity = new WarnTaskMsgEntity();
            BeanUtils.copyProperties(data, entity);
            Integer appId = data.getAppId();
            App app = appService.findById(appId);
            if (app != null) {
                entity.setAppName(app.getAppName());
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
     * @param taskMsgData
     * @return
     */
    public boolean add(WarnTaskMsgData taskMsgData) {
        return taskMsgService.add(taskMsgData);
    }

    /**
     * 更新消息 状态
     *
     * @param id
     * @param status 0:未读 1:已读
     * @return
     */
    public boolean updateStatus(String id, int status) {
        return taskMsgService.updateStatus(id, status);
    }

}
