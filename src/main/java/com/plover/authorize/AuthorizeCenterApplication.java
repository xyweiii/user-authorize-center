package com.plover.authorize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.FlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableRedisHttpSession(flushMode = FlushMode.ON_SAVE, maxInactiveIntervalInSeconds = 7200)
public class AuthorizeCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizeCenterApplication.class, args);
    }

    @Bean
    public HeaderHttpSessionIdResolver headerHttpSessionIdResolver() {
        return new HeaderHttpSessionIdResolver("token") {
            @Override
            public List<String> resolveSessionIds(HttpServletRequest request) {
                String token = request.getParameter("token");
                return token != null ? Collections.singletonList(token) : Collections.emptyList();
            }
        };
    }
}
