package com.plover.authorize.biz;

import com.plover.authorize.entity.SqlApiEntity;
import com.plover.authorize.model.SqlApi;
import com.plover.authorize.service.SqlApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project:p804-sql-api
 * Package: com.x04.sql.biz
 *
 * @author : xywei
 * @date : 2021-03-01
 */
@Component
@Slf4j
public class SqlApiBiz implements InitializingBean {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static JdbcTemplate jdbcTemplate;

    @Value("${sqlApi.datasource.url}")
    private String url;

    @Value("${sqlApi.datasource.username}")
    private String username;

    @Value("${sqlApi.datasource.password}")
    private String password;

    @Value("${sqlApi.query.host}")
    private String host;

    @Value("${sqlApi.query.port}")
    private String port;

    @Autowired
    private SqlApiService sqlApiService;

    @Override
    public void afterPropertiesSet() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 列表查询
     *
     * @return
     */
    public List<SqlApiEntity> list() {
        List<SqlApi> sqlApiList = sqlApiService.list();
        return convert(sqlApiList);
    }

    /**
     * 数据转换
     *
     * @param sqlApiList
     * @return
     */
    public List<SqlApiEntity> convert(List<SqlApi> sqlApiList) {
        return sqlApiList.stream().map(sqlApi -> {
            SqlApiEntity entity = new SqlApiEntity();
            BeanUtils.copyProperties(sqlApi, entity);
            if (sqlApi.getCreateDate() != null) {
                entity.setCreateDate(DateFormatUtils.format(sqlApi.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (sqlApi.getUpdateDate() != null) {
                entity.setUpdateDate(DateFormatUtils.format(sqlApi.getUpdateDate(), DATE_TIME_FORMAT));
            }
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 新增
     *
     * @param sqlApi
     * @return
     */
    public synchronized int add(SqlApi sqlApi) {
        //最新的一条
        SqlApi newOne = sqlApiService.findNewOne();
        Integer id = 1;
        if (newOne != null) {
            id = newOne.getId() + 1;
        }
        sqlApi.setId(id);
        sqlApi.setApiUrl("http://" + host + ":" + port + "/sqlApi/query/" + id);
        return sqlApiService.add(sqlApi);
    }

    /**
     * 更新
     *
     * @param sqlApi
     * @return
     */
    public int update(SqlApi sqlApi) {
        return sqlApiService.update(sqlApi);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    public int deleteById(Integer id) {
        if (id == null) {
            return 0;
        }
        return sqlApiService.deleteById(id);
    }


    /**
     * 查询
     *
     * @param id
     * @return
     */
    public List<Map<String, Object>> query(Integer id) {
        if (id == null) {
            return new ArrayList<>();
        }
        SqlApi sqlApi = sqlApiService.findById(id);
        if (sqlApi == null || StringUtils.isBlank(sqlApi.getSqlStr())) {
            return null;
        }
        log.info("query sql :{}", sqlApi.getApiUrl());
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlApi.getSqlStr());
        return result;
    }
}
