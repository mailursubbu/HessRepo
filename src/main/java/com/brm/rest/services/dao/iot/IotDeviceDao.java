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
import com.brm.services.iot.model.IotDeviceCreate;
import com.brm.services.iot.model.IotDeviceCreateResp;

@Component
public class IotDeviceDao {

    private final Logger log = LoggerFactory.getLogger(IotDeviceDao.class);

	@Autowired
	RestTemplate iotRestTemplate;
	
	
	
    @Value("${iot.device.create.endpoint}")
    private String iotDeviceCreateEndPt;
    
	public void provisionIotDevice(AccountInfo accInfo,String iotAccId){
		IotDeviceCreate iotDeviceCreate = new IotDeviceCreate( accInfo, iotAccId);
		
		
		
		HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Object> entity = new HttpEntity<Object>(iotDeviceCreate, headers);
        
        try{
        	ResponseEntity<IotDeviceCreateResp> response = iotRestTemplate.exchange(
            		iotDeviceCreateEndPt, HttpMethod.POST, entity,
            		IotDeviceCreateResp.class);
        	
            log.debug("Response recived from iot account creation is "+response.getBody());
            
            String success = response.getBody().getSuccess();
            if(success==null || !success.equals("yes")){
            	log.error("IOT account createion failed :"+response.getBody().getMessage());
            	throw new InvalidRequest("IOT account createion failed :"+
            	response.getBody().getMessage());
            }
            
        }catch(Exception e){
        	log.error("IOT device creation failed",e);
        	throw new InvalidRequest("IOT device createion failed",e);
        }
	}
	
    /*private final Logger log = LoggerFactory.getLogger(IotDeviceDao.class);

	@Autowired
	RestTemplate iotRestTemplate;
	
    @Value("${iot.device.create.endpoint}")
    private String iotAccCreateEndPt;
    
	public String provisionIotDevice(AccountInfo accInfo,String iotAccId){
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
	}*/
	
}
