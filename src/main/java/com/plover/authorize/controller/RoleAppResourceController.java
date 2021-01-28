package com.plover.authorize.controller;

import com.plover.authorize.biz.RoleAppResourceBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import com.plover.authorize.data.RoleAppResourceData;
import com.plover.authorize.entity.RoleAppResourceEntity;
import com.plover.authorize.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.controller
 * <p>
 * 角色-应用资源 相关接口
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@RestController
@RequestMapping("/roleAppResource")
@Slf4j
public class RoleAppResourceController {

    @Autowired
    private RoleAppResourceBiz roleAppResourceBiz;

    /**
     * 根据 roleId,appId 查询资源绑定关系
     *
     * @param roleId
     * @param appId
     * @return
     */
    @GetMapping("/findByRoleIdAndAppId")
    public Response<RoleAppResourceEntity> findByRoleIdAndAppId(@RequestParam("roleId") Integer roleId,
                                                                @RequestParam("appId") Integer appId) {
        Response<RoleAppResourceEntity> resp = new Response<>();
        try {
            RoleAppResourceEntity entity = roleAppResourceBiz.findByRoleIdAndAppId(roleId, appId);
            resp.setData(entity);
        } catch (Exception e) {
            log.error("roleAppResource findByRoleIdAndAppId occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    /**
     * 保存 角色-资源 对应关系
     *
     * @param roleAppResourceData
     * @param request
     * @return
     */
    @PostMapping("/save")
    public Response<Boolean> save(@RequestBody RoleAppResourceData roleAppResourceData, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            roleAppResourceData.setCreateBy(user.getUserName());
            roleAppResourceData.setUpdateBy(user.getUserName());
            boolean result = roleAppResourceBiz.save(roleAppResourceData);
            if (!result) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("操作失败");
            }
        } catch (Exception e) {
            log.error("roleAppResource save occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
}
