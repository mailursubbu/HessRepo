package com.cspire.prov.mobi.req;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cspire.prov.framework.model.mobi.MobiReqPayload;
import com.cspire.prov.framework.model.mobi.MobiResponse;
import com.cspire.prov.framework.model.mobi.MobitvReq;

@Component
public class ProcessMobiRequest {
    private static final Logger log = LoggerFactory.getLogger(ProcessMobiRequest.class);

    @Autowired
    RestTemplate mobiRestTemplate;

    @Value("${mobi.endpoint}")
    private String mobiEndpoint;

    @Value("${mobi.operator}")
    private String mobiOperator;
    @Value("${mobi.billingSystem}")
    private String mobiBillingSystem;
    
    @Value("${mobi.partner}")
    private String mobiPartner;

    //${mobi.server}/external/platform/v5/purchase/{operator}/{billing_system}/{external_id}/purchases/create.json?partner={partner}&ts={ts}&sig={sig}
    
    public MobiReqPayload generateMobiPayload(MobitvReq req) {
        MobiReqPayload mobiReq = new MobiReqPayload();
        mobiReq.setPurchase(req.getPurchase());
        return mobiReq;
    }

    public ResponseEntity<MobiResponse>  processMobiRequest(MobitvReq req) {       
            HttpEntity<Object> entity = prepareRpcEntity(req);
            
            ResponseEntity<MobiResponse> response = mobiRestTemplate.exchange(
                    mobiEndpoint, HttpMethod.POST, entity,
                    MobiResponse.class,mobiOperator,mobiBillingSystem,
                    req.getAccountNum().trim(),mobiPartner,System.currentTimeMillis()/1000L,"Signature");            
            return response;
    }

    private HttpEntity<Object> prepareRpcEntity(MobitvReq req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        MobiReqPayload mobiReq = this.generateMobiPayload(req);
        HttpEntity<Object> entity = new HttpEntity<Object>(mobiReq, headers);
        return entity;
    }
}
