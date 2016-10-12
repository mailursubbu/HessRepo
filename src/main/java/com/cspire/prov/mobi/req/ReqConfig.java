package com.cspire.prov.mobi.req;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class ReqConfig {
    @Scope(value=WebApplicationContext.SCOPE_REQUEST,proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Bean
    public ReqInfo reqInfo() {
        ReqInfo reqInfo = new ReqInfo();
        return reqInfo;
    }
    
    @Bean
    public RestTemplate mobiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
    
    @Bean
    public ProcessMobiRequest processMobiRequest(){
        ProcessMobiRequest processMobiRequest=new ProcessMobiRequest();
        return processMobiRequest;
    }
}
