package com.plover.user.common;

import lombok.Getter;

/**
 * @author : xywei
 */
@Getter
public enum HttpBizCode {

    SUCCESS(0, "SUCCESS"),
    NOTLOGIN(201, "用户未登陆"),
    ILLEGAL(202, "参数不合法"),
    SYSERROR(204, "系统异常"),
    BIZERROR(210, "业务异常"),
    NOT_EXISTS(211, "数据不存在");

    private int code;
    private String text;

    HttpBizCode(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
