package com.plover.authorize.service;

import com.plover.authorize.model.App;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.service
 *
 * @author : xywei
 * @date : 2021-01-19
 */
public interface AppService {

    /**
     * 获取所有app
     *
     * @return
     */
    List<App> list();

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    App findById(Integer id);

    /**
     * 新增
     *
     * @param app
     * @return
     */
    int add(App app);

    /**
     * 更新
     *
     * @param app
     * @return
     */
    int update(App app);

    /**
     * 删除
     *
     * @param id
     * @param updateBy 更新人
     * @return
     */
    int delete(Integer id, String updateBy);

    /**
     * 泥融 新增 app
     *
     * @param app
     * @return
     */
    int addByNiRong(App app);
}
