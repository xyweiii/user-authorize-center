package com.plover.authorize.service.impl;


import com.plover.authorize.mapper.SqlApiMapper;
import com.plover.authorize.model.SqlApi;
import com.plover.authorize.service.SqlApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project:p804-sql-api
 * Package: com.x04.sql.service.impl
 *
 * @author : xywei
 * @date : 2021-03-01
 */
@Service
public class SqlApiServiceImpl implements SqlApiService {


    @Autowired
    private SqlApiMapper sqlApiMapper;

    /**
     * 列表 查询
     *
     * @return
     */
    @Override
    public List<SqlApi> list() {
        return sqlApiMapper.list();
    }

    /**
     * 获取最新的一条数据
     *
     * @param
     * @return
     */
    @Override
    public SqlApi findNewOne() {
        return sqlApiMapper.findNewOne();
    }

    /**
     * 根据id查询
     *
     * @param id@return
     */
    @Override
    public SqlApi findById(Integer id) {
        if (id == null) {
            return null;
        }
        return sqlApiMapper.findById(id);
    }

    /**
     * 新增
     *
     * @param sql2Api
     * @return
     */
    @Override
    public int add(SqlApi sql2Api) {
        return sqlApiMapper.add(sql2Api);
    }

    /**
     * 更新
     *
     * @param sql2Api
     * @return
     */
    @Override
    public int update(SqlApi sql2Api) {
        return sqlApiMapper.update(sql2Api);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Override
    public int deleteById(Integer id) {
        if (id == null) {
            return 0;
        }
        return sqlApiMapper.deleteById(id);
    }
}
