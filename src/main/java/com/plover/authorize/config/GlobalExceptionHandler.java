package com.plover.authorize.config;

import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常捕捉处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Response defaultExceptionHandler(Exception e) {
        log.error("request occur error", e);
        Response resp = new Response<>();
        resp.setCode(HttpBizCode.BIZERROR.getCode());
        resp.setMessage(e.getMessage());
        return resp;
    }
}