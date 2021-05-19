package com.plover.authorize.controller;

import com.plover.authorize.biz.WarnTaskMsgBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.PageList;
import com.plover.authorize.common.Response;
import com.plover.authorize.entity.WarnTaskMsgEntity;
import com.plover.authorize.form.WarnTaskMsgQueryForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 预警任务相关 接口
 */
@RestController
@RequestMapping("/warnTaskMsg")
@Slf4j
public class WarnTaskMsgController {

    @Autowired
    private WarnTaskMsgBiz warnTaskMsgBiz;

    /**
     * 预警消息  列表
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Response<PageList<WarnTaskMsgEntity>> list(@RequestBody WarnTaskMsgQueryForm queryForm) {
        Response<PageList<WarnTaskMsgEntity>> resp = new Response<>();
        try {
            PageList<WarnTaskMsgEntity> pageList = warnTaskMsgBiz.list(queryForm);
            resp.setData(pageList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTaskMsg list occur error,queryForm:[{}]", queryForm, e);
        }
        return resp;
    }

    /**
     * 更新状态
     *
     * @param id
     * @param status 0:未读 1:已读
     * @return
     */
    @PostMapping("/updateStatus")
    public Response<Boolean> updateStatus(String id, Integer status) {
        Response<Boolean> resp = new Response<>();
        try {
            if (StringUtils.isBlank(id) || status == null) {
                resp.setCode(HttpBizCode.ILLEGAL.getCode());
                resp.setMessage("id和status 不能为空");
                return resp;
            }
            boolean result = warnTaskMsgBiz.updateStatus(id, status);
            if (result) {
                resp.setData(true);
            } else {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("操作失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTaskMsg updateStatus occur error,", e);
        }
        return resp;
    }
}
