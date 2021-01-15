package com.plover.user.controller;

import com.plover.user.common.Constants;
import com.plover.user.common.HttpBizCode;
import com.plover.user.common.Response;
import com.plover.user.model.Staff;
import com.plover.user.model.User;
import com.plover.user.service.StaffService;
import com.plover.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Hashtable;

/**
 * Project:user-center
 * Package: com.plover.user.controller
 *
 * @author : xywei
 * @date : 2021-01-11
 */
@RestController
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private StaffService staffService;

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 工作人员登陆
     * 用户登陆统一认证平台
     *
     * @return
     */
    @PostMapping(value = "/plat/login")
    public Response<String> login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpServletRequest request) {
        log.info("user login , userName:{},password:{}", userName, password);
        Response<String> resp = new Response<>();
        userName = userName.trim();
        password = password.trim();
        try {
            if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
                resp.fill(HttpBizCode.ILLEGAL, "用户名和密码不能为空");
                return resp;
            }
            Staff staff = staffService.findByUserName(userName);
            if (staff == null) {
                resp.fill(HttpBizCode.ILLEGAL, "用户不存在");
                return resp;
            }
            Boolean checkDomain = this.checkDomain(userName, password);
            //域登陆成功
            if (checkDomain) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", staff);
                resp.setData(session.getId());
            } else {
                resp.setCode(HttpBizCode.ILLEGAL.getCode());
                resp.setMessage("身份校验失败");
            }
        } catch (Exception e) {
            log.error("login occur error");
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage("业务异常,请稍后重试");
        }
        return resp;
    }

    /**
     * Domain 校验
     *
     * @param uid uid
     * @param pwd
     * @return
     */
    public boolean checkDomain(String uid, String pwd) {
        if (!StringUtils.equals("prod", env)) {
            return true;
        }
        //AD域ip
        String host = "10.123.0.7";
        //域名 后缀
        String domain = "@804.sast.casc";
        String port = "389";
        String url = new String("ldap://" + host + ":" + port);
        //验证的身份
        String user = uid.indexOf(domain) > 0 ? uid : uid + domain;
        Hashtable<String, String> env = new Hashtable<>();
        DirContext ctx = null;
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, pwd);
        //LDAP 工厂类
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        try {
            ctx = new InitialDirContext(env);
            log.info("身份验证成功");
        } catch (Exception e) {
            log.error("身份验证失败", e);
            return false;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                    log.error("ctx close occur error", e);
                }
            }
        }
        return true;
    }

    /**
     * 根据 domain 信息 创建 user
     *
     * @return
     */
    public synchronized User handleDomainUser(String userName, String pwd) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            user = new User();
            user.setUserName(userName);
            user.setPassword(pwd);
            user.setCreateBy(Constants.SYSTEM_DEFAULT_USER);
            user.setUpdateBy(Constants.SYSTEM_DEFAULT_USER);
            userService.add(user);
            user = userService.findByUserName(userName);
        }
        return user;
    }

    /**
     * 登陆权限认证管理后台
     *
     * @return
     */
    @PostMapping(value = "/manage/login")
    public Response<String> manageLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpServletRequest request) {
        log.info("user login , userName:{},password:{}", userName, password);
        Response<String> resp = new Response<>();
        userName = userName.trim();
        password = password.trim();
        try {
            if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
                resp.fill(HttpBizCode.ILLEGAL, "用户名和密码不能为空");
                return resp;
            }
            User user = userService.findByUserName(userName);
            if (user == null) {
                resp.fill(HttpBizCode.ILLEGAL, "用户不存在");
                return resp;
            }
            if (!StringUtils.equals(password, user.getPassword())) {
                resp.fill(HttpBizCode.ILLEGAL, "密码不正确");
                return resp;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            resp.setData(session.getId());
            return resp;
        } catch (Exception e) {
            log.error("login occur error");
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage("业务异常,请稍后重试");
        }
        return resp;
    }
}
