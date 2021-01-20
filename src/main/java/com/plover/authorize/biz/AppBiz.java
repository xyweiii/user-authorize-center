package com.plover.authorize.biz;

import com.plover.authorize.entity.AppEntity;
import com.plover.authorize.model.App;
import com.plover.authorize.service.AppService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project:user-center
 * Package: com.plover.user.biz
 *
 * @author : xywei
 * @date : 2021-01-19
 */
@Component
public class AppBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private AppService appService;

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
        return appList.stream().map(staff -> {
            AppEntity appEntity = new AppEntity();
            BeanUtils.copyProperties(staff, appEntity);
            if (staff.getCreateDate() != null) {
                appEntity.setCreateDate(DateFormatUtils.format(staff.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (staff.getUpdateDate() != null) {
                appEntity.setUpdateDate(DateFormatUtils.format(staff.getUpdateDate(), DATE_TIME_FORMAT));
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
