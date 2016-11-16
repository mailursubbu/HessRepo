package com.cspire.prov.mobi.req;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
@Configuration
public class SslRestClient {
    @Bean
    RestTemplate mobiRestTemplate(){
        RestTemplate sslRestTemplate = 
                new RestTemplate(httpComponentsClientHttpRequestFactory());
        return sslRestTemplate;
    }
    @Bean
    HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(){
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = 
                new HttpComponentsClientHttpRequestFactory();
        return httpComponentsClientHttpRequestFactory;
    }
}
