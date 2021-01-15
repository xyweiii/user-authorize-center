package com.plover.user.config;

import com.plover.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author xywei
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.info(" LoginInterceptor: session is  null");
            return201(response);
            return false;
        }
        session.getAttributeNames().hasMoreElements();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            log.info(" LoginInterceptor: userId is  null");
            return201(response);
        }
        return true;
    }

    /**
     * 校验session,为空返回 未登录信息
     *
     * @param response
     * @throws IOException
     */
    public void return201(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.println("{\"code\":201,\"data\":\"\",\"message\":\"用户未登陆\"}");
        writer.flush();
        writer.close();
    }
}
