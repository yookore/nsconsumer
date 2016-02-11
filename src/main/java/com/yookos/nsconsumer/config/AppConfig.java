package com.yookos.nsconsumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    RestTemplate restTemplate(){
        RestTemplate template = new RestTemplate();
        return template;
    }
}
