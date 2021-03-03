package com.plover.authorize.mapper;


import com.plover.authorize.model.SqlApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SqlApiMapper {

    /**
     * 列表查询
     *
     * @return
     */
    List<SqlApi> list();


    /**
     * 查询最新 一条记录
     *
     * @return
     */
    SqlApi findNewOne();

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    SqlApi findById(@Param("id") Integer id);

    /**
     * 新增
     *
     * @param sql2Api
     * @return
     */
    int add(SqlApi sql2Api);

    /**
     * 更新
     *
     * @param sql2Api
     * @return
     */
    int update(SqlApi sql2Api);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(@Param("id") Integer id);
}