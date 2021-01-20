package com.plover.authorize.controller;

import com.plover.authorize.biz.StaffRoleBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import com.plover.authorize.entity.StaffRoleEntity;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.controller
 *
 * @author : xywei
 * @date : 2021-01-14
 */
@RestController
@RequestMapping("/staffRole")
@Slf4j
public class StaffRoleController {

    @Autowired
    private StaffRoleBiz staffRoleBiz;

    /**
     * 查询staff 列表页
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Response<List<StaffRoleEntity>> list(HttpServletRequest request) {
        Response<List<StaffRoleEntity>> resp = new Response<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setCode(HttpBizCode.NOTLOGIN.getCode());
            resp.setMessage("用户未登陆");
            return resp;
        }
        try {
            List<StaffRoleEntity> entityList = staffRoleBiz.list();
            resp.setData(entityList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("staff list occur error", e);
        }
        return resp;
    }

    /**
     * 新增
     *
     * @param staffRole
     * @param request
     * @return
     */
    @PostMapping(value = "/add")
    public Response<Boolean> add(@RequestBody StaffRole staffRole, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            staffRole.setCreateBy(user.getUserName());
            staffRole.setUpdateBy(user.getUserName());
            int result = staffRoleBiz.add(staffRole);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("新增失败");
            }
        } catch (Exception e) {
            log.error("staffRole add occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }


    /**
     * 更新
     *
     * @param staffRole
     * @param request
     * @return
     */
    @PostMapping(value = "/update")
    public Response<Boolean> update(@RequestBody StaffRole staffRole, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            staffRole.setUpdateBy(user.getUserName());
            int result = staffRoleBiz.update(staffRole);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("更新失败");
            }
        } catch (Exception e) {
            log.error("staffRole update occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
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
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            int result = staffRoleBiz.delete(id, user.getUpdateBy());
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("删除失败");
            }
        } catch (Exception e) {
            log.error("staffRole delete occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
}
