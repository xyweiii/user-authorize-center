package com.plover.user.service.impl;

import com.plover.user.form.UserQueryForm;
import com.plover.user.mapper.UserMapper;
import com.plover.user.model.User;
import com.plover.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.plover.user.common.Constants.*;

/**
 * Project:user-center
 * Package: com.plover.user.service.impl
 *
 * @author : xywei
 * @date : 2021-01-11
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService, InitializingBean {

    @Autowired
    private UserMapper userMapper;

    /**
     * list
     *
     * @param queryForm
     * @return
     */
    @Override
    public List<User> list(UserQueryForm queryForm) {
        return userMapper.list(queryForm);
    }

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    @Override
    public int count(UserQueryForm queryForm) {
        return userMapper.count(queryForm);
    }

    /**
     * 根据id 查找
     *
     * @param id
     * @return
     */
    @Override
    public User findById(Integer id) {
        if (id == null) {
            return null;
        }
        return userMapper.findById(id);
    }

    /**
     * 根据userName进行查找
     *
     * @param userName
     * @return
     */
    @Override
    public User findByUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        return userMapper.findByUserName(userName);
    }

    /**
     * 新增
     *
     * @param user
     * @return
     */
    @Override
    public int add(User user) {
        return userMapper.add(user);
    }

    /**
     * 更新
     *
     * @param user
     * @return
     */
    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    @Override
    public int deleteById(Integer id, String updateBy) {
        return 0;
    }


    /**
     * init system user admin/123456
     */
    @Override
    public void afterPropertiesSet() {
        User user = userMapper.findByUserName(SYSTEM_ADMIN_USER);
        if (user == null) {
            User admin = new User();
            admin.setId(1);
            admin.setUserName(SYSTEM_ADMIN_USER);
            admin.setPassword(SYSTEM_ADMIN_PASSWORD);
            admin.setRole(SYSTEM_ADMIN_ROLE);
            admin.setCreateDate(new Date());
            admin.setCreateBy(SYSTEM_DEFAULT_USER);
            admin.setUpdateBy(SYSTEM_DEFAULT_USER);
            admin.setUpdateDate(new Date());
            userMapper.add(admin);
            log.info("user-center system user:[{}] inited", admin);
        }
    }
}
