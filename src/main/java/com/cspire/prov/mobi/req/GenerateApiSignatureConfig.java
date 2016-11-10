package com.cspire.prov.mobi.req;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class GenerateApiSignatureConfig {
    @Bean
    public GenerateApiSignature genApiSig(){
        GenerateApiSignature genApiSig =
                new GenerateApiSignature();
        return genApiSig;
    }
}
