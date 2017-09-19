package com.brm.rest.services.dao.iot;

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

import com.brm.service.portal.bean.customer.AccountInfo;
import com.brm.services.iot.model.InvalidRequest;
import com.brm.services.iot.model.IotAccCreate;
import com.brm.services.iot.model.IotAccCreateResp;

@Component
public class IotAccountDao {

    private final Logger log = LoggerFactory.getLogger(IotAccountDao.class);

	@Autowired
	RestTemplate iotRestTemplate;
	
    @Value("${iot.account.create.endpoint}")
    private String iotAccCreateEndPt;
    
	public String provisionIotAccount(AccountInfo accInfo){
		IotAccCreate iotAccCreate = new IotAccCreate(accInfo);
		
		HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Object> entity = new HttpEntity<Object>(iotAccCreate, headers);
        
        try{
        	ResponseEntity<IotAccCreateResp> response = iotRestTemplate.exchange(
            		iotAccCreateEndPt, HttpMethod.POST, entity,
                    IotAccCreateResp.class);
        	
            log.error("Response recived from iot account creation is "+response.getBody());
            
            String success = response.getBody().getSuccess();
            if(success==null || !success.equals("yes")){
            	throw new InvalidRequest("IOT account createion failed");
            }
            return response.getBody().getData().getAccount().getId();
            
        }catch(Exception e){
        	log.info("IOT account creation failed",e);
        	throw new InvalidRequest("IOT account createion failed",e);
        }
	}
	
}
