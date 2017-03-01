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
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cspire.prov.framework.model.mobi.Account;
import com.cspire.prov.framework.model.mobi.MobiReqPayload;
import com.cspire.prov.framework.model.mobi.MobiResponse;
import com.cspire.prov.framework.model.mobi.MobitvReq;
import com.cspire.prov.framework.model.mobi.Purchase;
import com.cspire.prov.framework.model.mobi.WhatToDoWithComp;

@Component
public class ProcessMobiRequest {
    private static final Logger log = LoggerFactory.getLogger(ProcessMobiRequest.class);

    @Autowired
    RestTemplate mobiRestTemplate;

    @Autowired
    GenerateApiSignature genApiSig;
    
    @Autowired
    SignatureForQuery querySig;
    
    @Autowired
    ReqInfo reqInfo;
    
    @Value("${mobi.endpoint}")
    private String mobiEndpoint;

    @Value("${mobi.query.endpoint}")
    private String mobiQueryEndpoint;

    
    @Value("${mobi.operator}")
    private String mobiOperator;
    @Value("${mobi.billingSystem}")
    private String mobiBillingSystem;
    
    @Value("${mobi.partner}")
    private String mobiPartner;
    
	@Value("${mobi.config.stream.code}")
	String streamCode;

	@Value("${mobi.config.dvr.code}")
	String dvrCode;

    private void updateVerndorPurchaseIdAndOrigin(MobitvReq req){
        Integer provId = reqInfo.getProvId();
        Integer servOrder = req.getServiceOrder();
        String verndorPurId = provId.toString()+"-"+servOrder.toString();
        log.trace("verndorPurId:{}",verndorPurId);
        Purchase[] purchases = req.getPurchase();
        String origin = req.getOrigin();
        for(Purchase pur:purchases){
            pur.setVendor_purchase_id(verndorPurId);
            String action = pur.getAction();
 
            if(action.equals(WhatToDoWithComp.CREATE.name().toLowerCase())){
                pur.setPurchase_origin(origin);
            }else{
                pur.setCancel_origin(origin);
            }            
        }
    }
        
    private MobiReqPayload generateMobiPayload(MobitvReq req) {
    	Account account  = new Account();
    	account.setStatus(req.getStatus());
        account.setFips_code(req.getFipsCode());
        
    	MobiReqPayload mobiReq = new MobiReqPayload();
        this.updateVerndorPurchaseIdAndOrigin(req);
        mobiReq.setPurchase(req.getPurchase());   
        mobiReq.setAccount(account);
        return mobiReq;
    }

    
    @Retryable
    public ResponseEntity<MobiResponse>  processMobiRequest(MobitvReq req) {       
            HttpEntity<Object> entity = prepareRpcEntity(req);
            String externalId = req.getAccountCode().trim();
            long ts = System.currentTimeMillis()/1000L;
            String sig = genApiSig.generateSigWithTs(externalId,ts);            
            ResponseEntity<MobiResponse> response = mobiRestTemplate.exchange(
                    mobiEndpoint, HttpMethod.POST, entity,
                    MobiResponse.class,
                    externalId,mobiPartner,ts,sig);  

            /*
             * Temporary code. To be removed on fixing mobi bug
             */
            HttpStatus  status = response.getStatusCode();
            if(status == HttpStatus.NOT_FOUND){
            	if(response.getBody() != null){
            		log.error("Temporary fix to change 404 status returned from Mobi Server to 400 status.");
                	ResponseEntity<MobiResponse> newResp = new ResponseEntity<MobiResponse>(response.getBody(),response.getHeaders(),HttpStatus.BAD_REQUEST);
                	return newResp;	
            	}
            }
            /*
             * End of temporary code
             */
            
            return response;
    }
    
    
    @Retryable
    public ResponseEntity<Object>  processMobiQueryRequest(String accCode) { 
            long ts = System.currentTimeMillis()/1000L;
            String sig = querySig.generateSigWithTs(accCode,ts);            
            ResponseEntity<Object> response = mobiRestTemplate.exchange(
            		mobiQueryEndpoint, HttpMethod.GET, null,
                    Object.class,
                    accCode,mobiPartner,ts,sig);    
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
