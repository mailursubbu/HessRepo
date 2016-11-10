package com.cspire.prov.mobi.req;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cspire.prov.framework.model.mobi.MobiReqPayload;
import com.cspire.prov.framework.model.mobi.MobiResponse;
import com.cspire.prov.framework.model.mobi.MobitvReq;
import com.cspire.prov.framework.model.mobi.Purchase;

@Component
public class ProcessMobiRequest {
    private static final Logger log = LoggerFactory.getLogger(ProcessMobiRequest.class);

    @Autowired
    RestTemplate mobiRestTemplate;

    @Autowired
    GenerateApiSignature genApiSig;
    
    @Autowired
    ReqInfo reqInfo;
    
    @Value("${mobi.endpoint}")
    private String mobiEndpoint;

    @Value("${mobi.operator}")
    private String mobiOperator;
    @Value("${mobi.billingSystem}")
    private String mobiBillingSystem;
    
    @Value("${mobi.partner}")
    private String mobiPartner;
    

    private void updateVerndorPurchaseId(MobitvReq req){
        Integer provId = reqInfo.getProvId();
        Integer servOrder = req.getServiceOrder();
        String verndorPurId = provId.toString()+"-"+servOrder.toString();
        Purchase[] purchases = req.getPurchase();
        for(Purchase pur:purchases){
            pur.setVendor_purchase_id(verndorPurId);
        }
    }
    //${mobi.server}/external/platform/v5/purchase/{operator}/{billing_system}/{external_id}/purchases/create.json?partner={partner}&ts={ts}&sig={sig}
    
    public MobiReqPayload generateMobiPayload(MobitvReq req) {
        MobiReqPayload mobiReq = new MobiReqPayload();
        
        this.updateVerndorPurchaseId(req);
        mobiReq.setPurchase(req.getPurchase());
        
        return mobiReq;
    }

    public ResponseEntity<MobiResponse>  processMobiRequest(MobitvReq req) {       
            HttpEntity<Object> entity = prepareRpcEntity(req);
            String externalId = req.getAccountNum()+"-"+req.getLocationId().toString();
            String sig = genApiSig.generateSig(externalId);
            
            ResponseEntity<MobiResponse> response = mobiRestTemplate.exchange(
                    mobiEndpoint, HttpMethod.POST, entity,
                    MobiResponse.class,mobiOperator,mobiBillingSystem,
                    req.getAccountNum().trim()+"-"+req.getLocationId().toString(),mobiPartner,System.currentTimeMillis()/1000L,sig);            
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
