package com.plover.user.controller;

import com.alibaba.excel.EasyExcel;
import com.plover.user.biz.StaffBiz;
import com.plover.user.biz.StaffImportListener;
import com.plover.user.common.HttpBizCode;
import com.plover.user.common.PageList;
import com.plover.user.common.Response;
import com.plover.user.entity.StaffEntity;
import com.plover.user.form.StaffQueryForm;
import com.plover.user.model.Staff;
import com.plover.user.model.User;
import com.plover.user.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Project:user-center
 * Package: com.plover.user.controller
 *
 * @author : xywei
 * @date : 2021-01-14
 */
@RestController
@RequestMapping("/staff")
@Slf4j
public class StaffController {


    @Autowired
    private StaffBiz staffBiz;

    @Autowired
    private StaffService staffService;

    /**
     * 查询staff 列表页
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Response<PageList<StaffEntity>> list(@RequestBody StaffQueryForm queryForm, HttpServletRequest request) {
        Response<PageList<StaffEntity>> resp = new Response<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setCode(HttpBizCode.NOTLOGIN.getCode());
            resp.setMessage("用户未登陆");
            return resp;
        }
        try {
            PageList<StaffEntity> pageList = staffBiz.list(queryForm);
            resp.setData(pageList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("staff list occur error,queryForm:[{}],user:[{}]", queryForm, user, e);
        }
        return resp;
    }

    /**
     * 新增
     *
     * @param staff
     * @param request
     * @return
     */
    @PostMapping(value = "/add")
    public Response<Boolean> add(@RequestBody Staff staff, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            staff.setCreateBy(user.getUserName());
            staff.setUpdateBy(user.getUserName());
            int result = staffBiz.add(staff);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("新增失败");
            }
        } catch (Exception e) {
            log.error("staff add occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }


    /**
     * 更新
     *
     * @param staff
     * @param request
     * @return
     */
    @PostMapping(value = "/update")
    public Response<Boolean> update(@RequestBody Staff staff, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                resp.setCode(HttpBizCode.NOTLOGIN.getCode());
                resp.setMessage("用户未登陆");
                return resp;
            }
            staff.setUpdateBy(user.getUserName());
            int result = staffBiz.update(staff);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("更新失败");
            }
        } catch (Exception e) {
            log.error("staff update occur error", e);
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
            int result = staffBiz.delete(id, user.getUpdateBy());
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("删除失败");
            }
        } catch (Exception e) {
            log.error("staff update occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    /**
     * 导入数据
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/importExcel")
    public Response<Boolean> importExcel(MultipartFile file) {
        Response<Boolean> resp = new Response<>();
        try {
            //异步处理
            CompletableFuture.runAsync(() -> {
                try {
                    EasyExcel.read(file.getInputStream(), Staff.class,
                            new StaffImportListener(staffService)).sheet().doRead();
                } catch (IOException e) {
                    log.error("EasyExcel read file occur error", e);
                }
            });
        } catch (Exception e) {
            log.error("staff importExcel occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
}
