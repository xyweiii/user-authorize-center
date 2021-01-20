package com.plover.authorize.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xywei
 */
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Autowired
    public CORSInterceptor corsInterceptor;

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor);
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/plat/login","/manage/login", "/user/getCurrentUser");
    }
}
