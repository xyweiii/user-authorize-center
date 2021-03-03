package com.plover.authorize.controller;


import com.plover.authorize.biz.SqlApiBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import com.plover.authorize.entity.SqlApiEntity;
import com.plover.authorize.model.SqlApi;
import com.plover.authorize.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Project:p804-sql-api
 * Package: com.x04.sql.controller
 *
 * @author : xywei
 * @date : 2021-03-01
 */
@RestController
@RequestMapping("/sqlApi")
@Slf4j
public class SqlApiController {

    @Autowired
    private SqlApiBiz sqlApiBiz;

    /**
     * 列表查询
     *
     * @return
     */
    @GetMapping("/list")
    public Response<List<SqlApiEntity>> list() {
        Response<List<SqlApiEntity>> resp = new Response<>();
        try {
            List<SqlApiEntity> entityList = sqlApiBiz.list();
            resp.setData(entityList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("sqlApi list occur error,", e);
        }
        return resp;
    }

    /**
     * 新增
     *
     * @param sqlApi
     * @return
     */
    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody SqlApi sqlApi, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");

            sqlApi.setCreateBy(user.getUserName());
            sqlApi.setUpdateBy(user.getUserName());
            int result = sqlApiBiz.add(sqlApi);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("新增失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("sqlApi add occur error,", e);
        }
        return resp;
    }

    /**
     * 更新
     *
     * @param sqlApi
     * @return
     */
    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody SqlApi sqlApi, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            sqlApi.setUpdateBy(user.getUserName());
            int result = sqlApiBiz.update(sqlApi);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("更新失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("sqlApi update occur error,", e);
        }
        return resp;
    }

    /**
     * 删除
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping(value = "/delete")
    public Response<Boolean> delete(@RequestParam Integer id, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            int result = sqlApiBiz.deleteById(id);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("删除失败");
            }
        } catch (Exception e) {
            log.error("app delete occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    /**
     * @param id
     * @return
     */
    @PostMapping(value = "/query/{id}")
    public Response<Object> query(@PathVariable("id") Integer id) {
        Response<Object> resp = new Response<>();
        try {
            List<Map<String, Object>> result = sqlApiBiz.query(id);
            resp.setData(result);
        } catch (Exception e) {
            log.error("sqlApi  query occur error,id:{}", id, e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
}
