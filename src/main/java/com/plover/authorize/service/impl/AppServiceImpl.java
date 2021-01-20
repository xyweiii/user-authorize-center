package com.plover.authorize.service.impl;

import com.plover.authorize.mapper.AppMapper;
import com.plover.authorize.model.App;
import com.plover.authorize.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.service.impl
 *
 * @author : xywei
 * @date : 2021-01-19
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppMapper appMapper;

    /**
     * 获取所有app
     *
     * @return
     */
    @Override
    public List<App> list() {
        return appMapper.list();
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public App findById(Integer id) {
        if (id == null) {
            return null;
        }
        return appMapper.findById(id);
    }

    /**
     * 新增
     *
     * @param app
     * @return
     */
    @Override
    public int add(App app) {
        return appMapper.add(app);
    }

    /**
     * 更新
     *
     * @param app
     * @return
     */
    @Override
    public int update(App app) {
        return appMapper.update(app);
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
        return appMapper.deleteById(id, updateBy);
    }
}
