package com.plover.authorize.service.impl;

import com.plover.authorize.form.AppResourceQueryForm;
import com.plover.authorize.mapper.AppResourceMapper;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.service.AppResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.service.impl
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@Service
public class AppResourceServiceImpl implements AppResourceService {


    @Autowired
    private AppResourceMapper resourceMapper;

    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    @Override
    public List<AppResource> list(AppResourceQueryForm queryForm) {
        return resourceMapper.list(queryForm);
    }

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    @Override
    public int count(AppResourceQueryForm queryForm) {
        return resourceMapper.count(queryForm);
    }

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    @Override
    public AppResource findById(Integer id) {
        if (id == null) {
            return null;
        }
        return resourceMapper.findById(id);
    }

    /**
     * 根据appId 查询
     *
     * @param appId
     * @return
     */
    @Override
    public List<AppResource> findByAppId(Integer appId) {
        if (appId == null) {
            return Collections.emptyList();
        }
        return resourceMapper.findByAppId(appId);
    }

    /**
     * 新增
     *
     * @param appResource
     * @return
     */
    @Override
    public int add(AppResource appResource) {
        return resourceMapper.add(appResource);
    }

    /**
     * 更新
     *
     * @param appResource
     * @return
     */
    @Override
    public int update(AppResource appResource) {
        return resourceMapper.update(appResource);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    @Override
    public int delete(Integer id, String updateBy) {
        if (id == null) {
            return 0;
        }
        return resourceMapper.deleteById(id, updateBy);
    }
}
