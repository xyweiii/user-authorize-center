package com.plover.authorize.service;

import com.plover.authorize.form.AppResourceQueryForm;
import com.plover.authorize.model.AppResource;

import java.util.List;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.service
 *
 * @author : xywei
 * @date : 2021-01-26
 */
public interface AppResourceService {


    /**
     * 列表查询
     *
     * @return
     */
    List<AppResource> list(AppResourceQueryForm queryForm);


    /**
     * count
     *
     * @return
     */
    int count(AppResourceQueryForm queryForm);


    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    AppResource findById(Integer id);


    /**
     * 根据appId 查询
     *
     * @param appId
     * @return
     */
    List<AppResource> findByAppId(Integer appId);

    /**
     * 新增
     *
     * @param appResource
     * @return
     */
    int add(AppResource appResource);

    /**
     * 更新
     *
     * @param appResource
     * @return
     */
    int update(AppResource appResource);

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    int delete(Integer id, String updateBy);
}
