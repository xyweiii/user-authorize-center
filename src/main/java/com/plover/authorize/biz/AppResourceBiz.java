package com.plover.authorize.biz;

import com.plover.authorize.common.PageList;
import com.plover.authorize.entity.AppEntity;
import com.plover.authorize.entity.AppResourceEntity;
import com.plover.authorize.entity.StaffEntity;
import com.plover.authorize.form.AppResourceQueryForm;
import com.plover.authorize.model.App;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.AppResourceService;
import com.plover.authorize.service.AppService;
import com.plover.authorize.service.RoleAppResourceService;
import com.plover.authorize.service.StaffRoleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.biz
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@Component
public class AppResourceBiz {


    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    @Autowired
    private AppService appService;

    @Autowired
    private AppResourceService resourceService;

    @Autowired
    private StaffRoleService roleService;

    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    public PageList<AppResourceEntity> list(AppResourceQueryForm queryForm) {
        PageList<AppResourceEntity> page = new PageList<>();
        List<AppResource> resourceList = resourceService.list(queryForm);
        List<AppResourceEntity> entityList = convert(resourceList);
        int totalCount = resourceService.count(queryForm);
        page.setPageNum(queryForm.pageNum);
        page.setPageSize(queryForm.pageSize);
        page.setTotal(totalCount);
        page.setDataList(entityList);
        return page;
    }


    /**
     * 根据appId获取
     *
     * @param appId
     * @return
     */
    public List<AppResource> findByAppId(Integer appId) {
        if (appId == null) {
            return Collections.emptyList();
        }
        return resourceService.findByAppId(appId);
    }







    /**
     * staff数据转换
     * appResource -->  appResourceEntity
     *
     * @param resourceList
     * @return
     */
    public List<AppResourceEntity> convert(List<AppResource> resourceList) {
        return resourceList.stream().map(resource -> {
            AppResourceEntity resourceEntity = new AppResourceEntity();
            BeanUtils.copyProperties(resource, resourceEntity);
            if (resource.getCreateDate() != null) {
                resourceEntity.setCreateDate(DateFormatUtils.format(resource.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (resource.getUpdateDate() != null) {
                resourceEntity.setUpdateDate(DateFormatUtils.format(resource.getUpdateDate(), DATE_TIME_FORMAT));
            }
            if (resource.getAppId() != null) {
                App app = appService.findById(resource.getAppId());
                resourceEntity.setApp(app);
            }
            return resourceEntity;
        }).collect(Collectors.toList());
    }


    /**
     * 新增
     *
     * @param appResource
     * @return
     */
    public int add(AppResource appResource) {
        return resourceService.add(appResource);
    }

    /**
     * update
     *
     * @param appResource
     * @return
     */
    public int update(AppResource appResource) {
        return resourceService.update(appResource);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    public int delete(Integer id, String updateBy) {
        return resourceService.delete(id, updateBy);
    }
}
