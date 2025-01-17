package com.plover.authorize.controller;

import com.plover.authorize.biz.AppBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import com.plover.authorize.entity.AppEntity;
import com.plover.authorize.entity.StaffEntity;
import com.plover.authorize.model.App;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.controller
 *
 * @author : xywei
 * @date : 2021-01-19
 */
@RestController
@RequestMapping("/app")
@Slf4j
public class AppController {

    @Autowired
    private AppBiz appBiz;

    /**
     * 获取当前用户被授权的App 应用
     */
    @GetMapping("/findAuthorizedAppByStaff")
    public Response<List<App>> findAuthorizedAppByStaff(HttpServletRequest request) {
        Response<List<App>> resp = new Response<>();
        Staff staff = (Staff) request.getSession().getAttribute("user");
        try {
            List<App> appList = appBiz.findAuthorizedAppByStaff(staff);
            resp.setData(appList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("app findAuthorizedAppByStaff occur error,staff:[{}]", staff, e);
        }
        return resp;
    }

    /**
     * 根据应用获取当前用户的 授权资源
     */
    @GetMapping("/findAppResourceByAppAndStaff")
    public Response<List<AppResource>> findAppResourceByAppAndStaff(Integer id, HttpServletRequest request) {
        Response<List<AppResource>> resp = new Response<>();
        Staff staff = (Staff) request.getSession().getAttribute("user");
        try {
            List<AppResource> appResourceList = appBiz.getRoleAppResourceInfo(id, staff);
            resp.setData(appResourceList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("app findAppResourceByAppAndStaff occur error,staff:[{}]", staff, e);
        }
        return resp;
    }


    /**
     * 查询所有 app 信息
     *
     * @return
     */
    @GetMapping(value = "/list")
    public Response<List<AppEntity>> list(HttpServletRequest request) {
        Response<List<AppEntity>> resp = new Response<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setCode(HttpBizCode.NOTLOGIN.getCode());
            resp.setMessage("用户未登陆");
            return resp;
        }
        try {
            List<AppEntity> appEntityList = appBiz.list();
            resp.setData(appEntityList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("app list occur error,user:[{}]", user, e);
        }
        return resp;
    }

    /**
     * 新增
     *
     * @param app
     * @param request
     * @return
     */
    @PostMapping(value = "/add")
    public Response<Boolean> add(@RequestBody App app, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            app.setCreateBy(user.getUserName());
            app.setUpdateBy(user.getUserName());
            int result = appBiz.add(app);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("新增失败");
            }
        } catch (Exception e) {
            log.error("app add occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    /**
     * 更新
     *
     * @param app
     * @param request
     * @return
     */
    @PostMapping(value = "/update")
    public Response<Boolean> update(@RequestBody App app, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            app.setUpdateBy(user.getUserName());
            int result = appBiz.update(app);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("更新失败");
            }
        } catch (Exception e) {
            log.error("app update occur error", e);
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
            int result = appBiz.delete(id, user.getUpdateBy());
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
     * 上传 轻应用文件
     *
     * @param file 文件
     * @param id   应用主键 id
     */
    @PostMapping(value = "/uploadFile")
    public Response<Boolean> importExcel(MultipartFile file, Integer id) {
        Response<Boolean> resp = new Response<>();
        try {
            appBiz.uploadFile(file, id);
        } catch (Exception e) {
            log.error("app uploadFile occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    /**
     * 泥融 通过接口 添加app
     *
     * @param app
     * @return
     */
    @PostMapping(value = "/addByNiRong")
    public Response<App> addByNiRong(@RequestBody App app) {
        Response<App> resp = new Response<>();
        try {
            App result = appBiz.addByNiRong(app);
            if (result == null) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("新增失败");
            } else {
                resp.setData(app);
            }
        } catch (Exception e) {
            log.error("app addByNiRong occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
}
