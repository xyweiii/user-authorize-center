package com.plover.authorize.controller;

import com.plover.authorize.biz.RoleAppResourceAuthorityBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import com.plover.authorize.data.RoleAppResourceAuthorityData;
import com.plover.authorize.entity.RoleAppResourceAuthorityEntity;
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
@RequestMapping("/roleAppResourceAuthority")
@Slf4j
public class RoleAppResourceAuthorityController {

    @Autowired
    private RoleAppResourceAuthorityBiz authorityBiz;

    /**
     * 根据 roleId,resourceId 查询 该资源 权限设置
     *
     * @param roleId
     * @param resourceId
     * @return
     */
    @GetMapping("/findByRoleIdAndResourceId")
    public Response<RoleAppResourceAuthorityEntity> findByRoleIdAndResourceId(@RequestParam("roleId") Integer roleId,
                                                                              @RequestParam("resourceId") Integer resourceId) {
        Response<RoleAppResourceAuthorityEntity> resp = new Response<>();
        try {
            RoleAppResourceAuthorityEntity entity = authorityBiz.findByRoleIdAndResourceId(roleId, resourceId);
            resp.setData(entity);
        } catch (Exception e) {
            log.error("roleAppResourceAuthority findByRoleIdAndResourceId occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    /**
     * 保存 资源 对应的 权限设置
     *
     * @param authorityData
     * @param request
     * @return
     */
    @PostMapping("/save")
    public Response<Boolean> save(@RequestBody RoleAppResourceAuthorityData authorityData, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            authorityData.setCreateBy(user.getUserName());
            authorityData.setUpdateBy(user.getUserName());
            boolean result = authorityBiz.save(authorityData);
            if (!result) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("操作失败");
            }
        } catch (Exception e) {
            log.error("roleAppResourceAuthority save occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
}
