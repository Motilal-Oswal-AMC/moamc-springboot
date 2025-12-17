package com.motilaloswal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync // This is crucial for background tasks to work
public class AppConfig {

    @Value("${aem.api.username}")
    private String aemUsername;

    @Value("${aem.api.password}")
    private String aemPassword;
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // This interceptor automatically adds the
        // "Authorization: Basic <base64_encoded_username:password>"
        // header to every request made by this RestTemplate.
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(aemUsername, aemPassword)
        );

        return restTemplate;
    }
}