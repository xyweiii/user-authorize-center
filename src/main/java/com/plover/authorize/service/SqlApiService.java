package com.plover.authorize.service;


import com.plover.authorize.model.SqlApi;

import java.util.List;

/**
 * Project:p804-sql-api
 * Package: com.x04.sql.service
 *
 * @author : xywei
 * @date : 2021-03-01
 */
public interface SqlApiService {

    /**
     * 列表 查询
     *
     * @return
     */
    List<SqlApi> list();

    /**
     * 获取最新的一条数据
     *
     * @param
     * @return
     */
    SqlApi findNewOne();


    /**
     * 根据id查询
     *
     * @param
     * @return
     */
    SqlApi findById(Integer id);

    /**
     * 新增
     *
     * @param sqlApi
     * @return
     */
    int add(SqlApi sqlApi);

    /**
     * 更新
     *
     * @param sqlApi
     * @return
     */
    int update(SqlApi sqlApi);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(Integer id);
}
