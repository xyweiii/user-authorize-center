package com.plover.authorize.controller;

import com.plover.authorize.biz.WarnTaskLogBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.PageList;
import com.plover.authorize.common.Response;
import com.plover.authorize.entity.WarnTaskLogEntity;
import com.plover.authorize.form.WarnTaskLogQueryForm;
import com.plover.authorize.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/warnTaskLog")
@Slf4j
public class WarnTaskLogController {


    @Autowired
    private WarnTaskLogBiz taskLogBiz;

    /**
     * 告警任务 执行日志 列表
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Response<PageList<WarnTaskLogEntity>> list(@RequestBody WarnTaskLogQueryForm queryForm, HttpServletRequest request) {
        Response<PageList<WarnTaskLogEntity>> resp = new Response<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resp.setCode(HttpBizCode.NOTLOGIN.getCode());
            resp.setMessage("用户未登陆");
            return resp;
        }
        try {
            PageList<WarnTaskLogEntity> pageList = taskLogBiz.list(queryForm);
            resp.setData(pageList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTaskLog list occur error,queryForm:[{}],user:[{}]", queryForm, user, e);
        }
        return resp;
    }

}
