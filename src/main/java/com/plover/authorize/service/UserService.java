package com.plover.authorize.service;

import com.plover.authorize.form.UserQueryForm;
import com.plover.authorize.model.User;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.service
 *
 * @author : xywei
 * @date : 2021-01-11
 */
public interface UserService {

    /**
     * list
     *
     * @param queryForm
     * @return
     */
    List<User> list(UserQueryForm queryForm);

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    int count(UserQueryForm queryForm);


    /**
     * 根据id 查找
     *
     * @param id
     * @return
     */
    User findById(Integer id);

    /**
     * 根据userName进行查找
     *
     * @param userName
     * @return
     */
    User findByUserName(String userName);


    /**
     * 新增
     *
     * @param user
     * @return
     */
    int add(User user);

    /**
     * 更新
     *
     * @param user
     * @return
     */
    int update(User user);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(Integer id, String updateBy);
}
