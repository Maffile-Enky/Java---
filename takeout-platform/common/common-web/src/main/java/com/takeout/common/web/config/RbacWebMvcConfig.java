package com.takeout.common.web.config;

import com.takeout.common.web.interceptor.RbacInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RbacWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RbacInterceptor())
                .addPathPatterns("/**")
                .order(1);
    }
}
