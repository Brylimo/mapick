package com.enjoytrip.config.web;

import com.enjoytrip.interceptor.SessionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfigurer implements WebMvcConfigurer {
    private final SessionInterceptor si;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*registry.addInterceptor(si)
                .addPathPatterns("/attractions/**", "/hotplaces/**", "/members/**", "/boards/**", "/plans/**")
                .excludePathPatterns("/members/loginForm", "/members/memberJoinForm", "/members/memberJoin", "/members/findPwFrom", "/members/login");*/
    }
}
