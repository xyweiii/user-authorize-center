package com.plover.authorize.controller;

import com.plover.authorize.biz.AppResourceBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.PageList;
import com.plover.authorize.common.Response;
import com.plover.authorize.entity.AppResourceEntity;
import com.plover.authorize.form.AppResourceQueryForm;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.controller
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@RestController
@RequestMapping("/appResource")
@Slf4j
public class AppResourceController {

    @Autowired
    private AppResourceBiz resourceBiz;

    /**
     * appResource 列表页
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Response<PageList<AppResourceEntity>> list(@RequestBody AppResourceQueryForm queryForm, HttpServletRequest request) {
        Response<PageList<AppResourceEntity>> resp = new Response<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setCode(HttpBizCode.NOTLOGIN.getCode());
            resp.setMessage("用户未登陆");
            return resp;
        }
        try {
            PageList<AppResourceEntity> pageList = resourceBiz.list(queryForm);
            resp.setData(pageList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("resource list occur error,queryForm:[{}],user:[{}]", queryForm, user, e);
        }
        return resp;
    }

    /**
     * 根据appId查询
     *
     * @param appId
     * @return
     */
    @GetMapping(value = "/findByAppId")
    public Response<List<AppResource>> findByAppId(@RequestParam Integer appId) {
        Response<List<AppResource>> resp = new Response<>();
        try {
            List<AppResource> appResourceList = resourceBiz.findByAppId(appId);
            resp.setData(appResourceList);
        } catch (Exception e) {
            log.error("resource add occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }


    /**
     * 新增
     *
     * @param resource
     * @param request
     * @return
     */
    @PostMapping(value = "/add")
    public Response<Boolean> add(@RequestBody AppResource resource, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            resource.setCreateBy(user.getUserName());
            resource.setUpdateBy(user.getUserName());
            int result = resourceBiz.add(resource);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("新增失败");
            }
        } catch (Exception e) {
            log.error("resource add occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }


    /**
     * 更新
     *
     * @param resource
     * @param request
     * @return
     */
    @PostMapping(value = "/update")
    public Response<Boolean> update(@RequestBody AppResource resource, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            resource.setUpdateBy(user.getUserName());
            int result = resourceBiz.update(resource);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("更新失败");
            }
        } catch (Exception e) {
            log.error("resource update occur error", e);
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
            int result = resourceBiz.delete(id, user.getUpdateBy());
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("删除失败");
            }
        } catch (Exception e) {
            log.error("resource delete occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
}
