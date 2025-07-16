package com.marketplace.catalogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    /**
     * It matches the name of the function with the name of parameter passed to the constructor
     * of each service client. This is a convention used in Spring to automatically inject
     * the correct WebClient instance into each service client.
     */


    @Bean
    public WebClient ariaClient() { // Auth service
        return WebClient.builder()
                .baseUrl("https://aria-team-collaboration-2.onrender.com")
                .build();
    }

    @Bean
    public WebClient metronomeClient() { // Stock service
        return WebClient.builder()
                .baseUrl("http://metronome-service")
                .build();
    }

    @Bean
    public WebClient scenaClient() { // Media service
        return WebClient.builder()
                .baseUrl("https://scena-service-lazycode12-lazycode12s-projects.vercel.app")
                .build();
    }

    @Bean
    public WebClient echoClient() { // Reviews service
        return WebClient.builder()
                .baseUrl("http://echo-service")
                .build();
    }

    @Bean
    public WebClient ornamentoClient() { // Promotions service
        return WebClient.builder()
                .baseUrl("https://ornamento-api-tjvp.vercel.app")
                .build();
    }

}
