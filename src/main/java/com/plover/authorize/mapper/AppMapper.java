package com.plover.authorize.mapper;

import com.plover.authorize.model.App;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Mapper
public interface AppMapper {


    /**
     * 获取所有应用
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
    int deleteById(@Param("id") Integer id, @Param("updateBy") String updateBy);
}