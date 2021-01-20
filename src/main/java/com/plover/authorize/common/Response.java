package com.plover.authorize.common;

/**
 * Project:nexgo-bill
 * File: com.nexgo.bill.constant
 *
 * @author : xywei
 * @date : 2020-05-05
 */

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 接口返回结果类
 *
 * @author dean
 */
@ToString
@Data
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -8912267628340132131L;

    private int code = HttpBizCode.SUCCESS.getCode();
    private String message = "SUCCESS";
    private T data;

    public Response() {
    }

    public Response(HttpBizCode code, String message, T data) {
        this.fill(code, message, data);
    }

    /**
     * 成功调用
     *
     * @param code
     * @param message
     * @param data
     * @return
     */
    public Response<T> fill(HttpBizCode code, String message, T data) {
        this.code = code == null ? HttpBizCode.SUCCESS.getCode() : code.getCode();
        this.message = message;
        this.data = data;
        return this;
    }

    public Response<T> fill(HttpBizCode code, String message) {
        this.code = code == null ? HttpBizCode.SUCCESS.getCode() : code.getCode();
        this.message = message;
        return this;
    }
}