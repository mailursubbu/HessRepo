package com.brm.rest.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class SslRestClient {
    @Bean
    RestTemplate iotRestTemplate(){
        RestTemplate sslRestTemplate = 
                new RestTemplate(
                        new BufferingClientHttpRequestFactory(
                        httpComponentsClientHttpRequestFactory()));
        
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new LoggingRequestInterceptor());
        sslRestTemplate.setInterceptors(interceptors);
        
        sslRestTemplate.setErrorHandler(new RestResponseErrorHandler());
        return sslRestTemplate;
    }
 
    @Bean
    HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(){
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = 
                new HttpComponentsClientHttpRequestFactory();
        return httpComponentsClientHttpRequestFactory;
    }
}
