package com.plover.user.biz;

import com.plover.user.common.PageList;
import com.plover.user.entity.UserEntity;
import com.plover.user.form.UserQueryForm;
import com.plover.user.model.User;
import com.plover.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project:nexgo-bill
 * File: com.nexgo.bill.biz
 *
 * @author : xywei
 * @date : 2020-05-05
 */
@Component
public class UserBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private UserService userService;


    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    public PageList<UserEntity> list(UserQueryForm queryForm) {
        PageList<UserEntity> page = new PageList<>();
        List<User> userList = userService.list(queryForm);
        List<UserEntity> entityList = convert(userList);
        int totalCount = userService.count(queryForm);
        page.setPageNum(queryForm.pageNum);
        page.setPageSize(queryForm.pageSize);
        page.setTotal(totalCount);
        page.setDataList(entityList);
        return page;
    }

    /**
     * 用户数据转换
     *
     * @param userList
     * @return
     */
    public List<UserEntity> convert(List<User> userList) {
        return userList.stream().map(user -> {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(user, userEntity);
            if (user.getCreateDate() != null) {
                userEntity.setCreateDate(DateFormatUtils.format(user.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (user.getUpdateDate() != null) {
                userEntity.setUpdateDate(DateFormatUtils.format(user.getUpdateDate(), DATE_TIME_FORMAT));
            }
            return userEntity;
        }).collect(Collectors.toList());
    }


    /**
     * 根据id查找用户
     *
     * @param id
     * @return
     */
    public User findById(Integer id) {
        if (id == null) {
            return null;
        }
        return userService.findById(id);
    }

    /**
     * 根据用户名查找
     *
     * @param userName
     * @return
     */
    public User findByUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        return userService.findByUserName(userName);
    }

    /**
     * 删除用户
     *
     * @param id
     * @param updateBy
     * @return
     */
    public int delete(Integer id, String updateBy) {
        return userService.deleteById(id, updateBy);
    }
}
