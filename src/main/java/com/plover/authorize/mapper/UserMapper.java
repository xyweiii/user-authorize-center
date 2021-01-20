package com.plover.authorize.mapper;

import com.plover.authorize.form.UserQueryForm;
import com.plover.authorize.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {

    /**
     * list 列表查询
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
     * 根据id 查询
     *
     * @param id
     * @return
     */
    User findById(Integer id);

    /**
     * 根据userName 查询
     *
     * @param userName
     * @return
     */
    User findByUserName(@Param("userName") String userName);

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
    int deleteById(@Param("id") Integer id, @Param("updateBy") String updateBy);
}