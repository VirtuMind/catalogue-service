package com.marketplace.catalogue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final AuthenticationInterceptor authenticationInterceptor;
    
    public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Apply authentication interceptor to all /products and /categories endpoints
        // The interceptor itself will skip GET requests as per OpenAPI spec
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/products/**", "/categories/**")
                .excludePathPatterns("/error");
    }
}
