package com.plover.user.controller;

import com.plover.user.biz.UserBiz;
import com.plover.user.common.HttpBizCode;
import com.plover.user.common.PageList;
import com.plover.user.common.Response;
import com.plover.user.entity.UserEntity;
import com.plover.user.form.UserQueryForm;
import com.plover.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Project:user-center
 * Package: com.plover.user.controller
 *
 * @author : xywei
 * @date : 2021-01-11
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserBiz userBiz;

    /**
     * 获取当前用户
     *
     * @return
     */
    @GetMapping(value = "/getCurrentUser")
    public Response getCurrentUser(HttpServletRequest request) {
        Response resp = new Response<>();
        Object object = request.getSession().getAttribute("user");
        if (object == null) {
            resp.setCode(HttpBizCode.NOT_EXISTS.getCode());
            resp.setMessage("user info not exist");
            return resp;
        }
        resp.setData(object);
        return resp;
    }

    /**
     * 查询用户 列表页
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Response<PageList<UserEntity>> list(@RequestBody UserQueryForm queryForm, HttpServletRequest request) {
        Response<PageList<UserEntity>> resp = new Response<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setCode(HttpBizCode.NOTLOGIN.getCode());
            resp.setMessage("用户未登陆");
            return resp;
        }
        try {
            PageList<UserEntity> pageList = userBiz.list(queryForm);
            resp.setData(pageList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("user list occur error,queryForm:[{}],user:[{}]", queryForm, user, e);
        }
        return resp;
    }
}
