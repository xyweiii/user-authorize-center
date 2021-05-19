package com.plover.authorize.controller;

import com.plover.authorize.biz.WarnTaskBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import com.plover.authorize.data.WarnTaskData;
import com.plover.authorize.entity.WarnTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预警任务相关 接口
 */
@RestController
@RequestMapping("/warnTask")
@Slf4j
public class WarnTaskController {

    @Autowired
    private WarnTaskBiz warnTaskBiz;

    /**
     * 预警 列表查询
     *
     * @return
     */
    @GetMapping("/list")
    public Response<List<WarnTaskEntity>> list() {
        Response<List<WarnTaskEntity>> resp = new Response<>();

        try {
            List<WarnTaskEntity> entityList = warnTaskBiz.list();
            resp.setData(entityList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTask list occur error,", e);
        }
        return resp;
    }

    /**
     * 新增
     *
     * @param warnTaskData
     * @return resp
     */

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody WarnTaskData warnTaskData) {
        Response<Boolean> resp = new Response<>();
        try {
            boolean result = warnTaskBiz.add(warnTaskData);
            if (result) {
                resp.setData(true);
            } else {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("操作失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTask add occur error,", e);
        }
        return resp;
    }

    /**
     * 更新
     *
     * @param warnTaskData
     * @return
     */
    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody WarnTaskData warnTaskData) {
        Response<Boolean> resp = new Response<>();
        try {
            boolean result = warnTaskBiz.update(warnTaskData);
            if (result) {
                resp.setData(true);
            } else {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("操作失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTask update occur error,", e);
        }
        return resp;
    }


    /**
     * 更新状态
     *
     * @param id
     * @param status 0:启用 1:
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
            boolean result = warnTaskBiz.updateStatus(id, status);
            if (result) {
                resp.setData(true);
            } else {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("操作失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTask updateStatus occur error,", e);
        }
        return resp;
    }

    /**
     * 更新状态
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Response<Boolean> delete(String id) {
        Response<Boolean> resp = new Response<>();
        try {
            if (StringUtils.isBlank(id)) {
                resp.setCode(HttpBizCode.ILLEGAL.getCode());
                resp.setMessage("id不能为空");
                return resp;
            }
            boolean result = warnTaskBiz.delete(id);
            if (result) {
                resp.setData(true);
            } else {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("操作失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("warnTask delete occur error,", e);
        }
        return resp;
    }
}
