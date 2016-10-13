package com.cspire.prov.mobi.xml.req;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.context.WebApplicationContext;

import com.cspire.prov.framework.xml.processor.XmlReqToObjProcessor;

@Configuration
public class MobiXmlPayloadConfig {

    @Bean
    public OmniaPayloadAdaptor omniaPayloadAdaptor(){
        OmniaPayloadAdaptor omniaPayloadAdaptor = new OmniaPayloadAdaptor();
        return omniaPayloadAdaptor;
    }
    
    @Bean
    public XmlReqToObjProcessor mobiPayloadProcessor() {
        XmlReqToObjProcessor handler = new XmlReqToObjProcessor();
        handler.setMarshaller(getApMaxMarshaller());
        handler.setUnmarshaller(getApMaxMarshaller());
        return handler;
    }

    @Bean
    public Jaxb2Marshaller getApMaxMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan("com.cspire.prov.framework.apmax.payload.jaxb");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jaxb.formatted.output", true);
        jaxb2Marshaller.setMarshallerProperties(map);
        return jaxb2Marshaller;
    }
}