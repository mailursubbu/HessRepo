package com.cspire.prov.mobi.req;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cspire.prov.framework.rest.client.LoggingRequestInterceptor;
import com.cspire.prov.framework.rest.client.RestResponseErrorHandler;
@Configuration
public class SslRestClient {
    @Bean
    RestTemplate mobiRestTemplate(){
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
