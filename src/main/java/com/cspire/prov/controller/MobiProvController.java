package com.cspire.prov.controller;

import java.io.IOException;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import com.cspire.prov.dtf.model.DailyTransFile;
import com.cspire.prov.dtf.model.DailyTransFileRepo;
import com.cspire.prov.dtf.model.IptvFipsCodeRepo;
import com.cspire.prov.framework.blackout.BlackoutService;
import com.cspire.prov.framework.exceptions.InvalidConfig;
import com.cspire.prov.framework.exceptions.InvalidRequest;
import com.cspire.prov.framework.exceptions.ServerInternalError;
import com.cspire.prov.framework.global.constants.Defaults;
import com.cspire.prov.framework.housekeeping.HouseKeepingErrorCodes;
import com.cspire.prov.framework.housekeeping.HouseKeepingService;
import com.cspire.prov.framework.housekeeping.HouseKeepingStatusCodes;
import com.cspire.prov.framework.model.ProvMngrResponse;
import com.cspire.prov.framework.model.RawXmlStringPayload;
import com.cspire.prov.framework.model.mobi.MobiResponse;
import com.cspire.prov.framework.model.mobi.MobitvReq;
import com.cspire.prov.framework.model.mobi.Purchase_response;
import com.cspire.prov.framework.model.mobi.WhatToDoWithComp;
import com.cspire.prov.framework.utils.UtilFuncs;
import com.cspire.prov.housekeeping.MobiHouseKeepingService;
import com.cspire.prov.mobi.req.ProcessMobiRequest;
import com.cspire.prov.mobi.req.ReqInfo;
import com.cspire.prov.mobi.xml.req.OmniaPayloadAdaptor;


@RestController
@RequestMapping("/rest")
public class MobiProvController {

    private static final Logger log = LoggerFactory.getLogger(MobiProvController.class);

    @Autowired
    MobiHouseKeepingService mobiHouseKeepingSer;
    @Autowired
    HouseKeepingService houseKeepingService;
    @Autowired
    UtilFuncs utils;
    @Autowired
    BlackoutService blackoutService;

    @Autowired
    ReqInfo reqInfo;

    @Autowired
    ProcessMobiRequest processMobiRequest;

    @Autowired
    OmniaPayloadAdaptor omniaPayloadAdaptor;
    
    @Autowired
    DailyTransFileRepo dtfRepo;
    

    
    @CrossOrigin
    @RequestMapping(value = "/mobi/omnia", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse mobiForOmniaProcessor(@RequestBody RawXmlStringPayload xmlRequest,
            HttpServletResponse resp) throws IOException {
        if (blackoutService.checkForBalckout("rest/mobi/omnia")) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.BLACKOUT, null, null,
                    ProvMngrResponse.FAIL_MSG, ProvMngrResponse.BLACKOUT_MSG, true);
        }              
        MobitvReq inputPayload=omniaPayloadAdaptor.omniaXmlToMobiReq(xmlRequest);        
        return receiveReqAndSetLoginfo(inputPayload, 
                false,
                inputPayload.getServiceRequestItemId(),
                resp,
                Defaults.DEFAULT_PROV_ID);
    }
    
    @CrossOrigin
    @RequestMapping(value = "/mobi/omniaSimulate", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse mobiForOmniaSimulate(@RequestBody RawXmlStringPayload xmlRequest,
            HttpServletResponse resp) throws IOException {
        if (blackoutService.checkForBalckout("rest/mobi/omniaSimulate")) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.BLACKOUT, null, null,
                    ProvMngrResponse.FAIL_MSG, ProvMngrResponse.BLACKOUT_MSG, true);
        }              
        MobitvReq inputPayload=omniaPayloadAdaptor.omniaXmlToMobiReq(xmlRequest,true);        
        return receiveReqAndSetLoginfo(inputPayload, 
                true,
                inputPayload.getServiceRequestItemId(),
                resp,
                Defaults.DEFAULT_PROV_ID);
    }
    
    
    @CrossOrigin
    @RequestMapping(value = "/mobi", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse mobiProvRequestProcessor(@RequestHeader("provId") Integer provId,
            @RequestBody MobitvReq inputPayload,
            HttpServletResponse resp) {
        // TODO: Implement the filter based blackout.
        if (blackoutService.checkForBalckout("rest/mobi")) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.BLACKOUT, null, null,
                    ProvMngrResponse.FAIL_MSG, ProvMngrResponse.BLACKOUT_MSG, true);
        }
        inputPayload.setIsValidationReq(false);
        // Try the readonly operations
        return receiveReqAndSetLoginfo(inputPayload, false,inputPayload.getServiceRequestItemId(),resp,provId);
    }


    @CrossOrigin
    @RequestMapping(value = "/mobiSimulate", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse mobiProvRequestValidator(@RequestHeader("provId") Integer provId,
            @RequestBody MobitvReq inputPayload,
            HttpServletResponse resp) {
        // TODO: Implement the filter based blackout.
        if (blackoutService.checkForBalckout("rest/mobiSimulate")) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.BLACKOUT, null, null,
                    ProvMngrResponse.FAIL_MSG, ProvMngrResponse.BLACKOUT_MSG, true);
        }

        inputPayload.setIsValidationReq(true);
        /*
         * Lets use orig req for all the read only txns with ApMax. maskedReq
         * would be used for all the write txns with ApMax. House Keeping would
         * be with orig request parameters.
         */
        return receiveReqAndSetLoginfo(inputPayload, true,inputPayload.getServiceRequestItemId(),resp,provId);
    }
    	
    private void validateServiceOrderItem(MobitvReq inputPayload){
        Integer serviceRequestItemId = inputPayload.getServiceRequestItemId();
        if(serviceRequestItemId==null){
            log.error("Input payload is missing serviceRequestItemId field");
            throw new InvalidRequest("Input payload is missing serviceRequestItemId field");            
        }
    }
    
    private ProvMngrResponse receiveReqAndSetLoginfo(MobitvReq inputPayload, 
            Boolean isValidateReq,Integer serviceRequestItemId,
            HttpServletResponse resp,Integer provId) {       
        reqInfo.setIsValidationReq(isValidateReq);
        reqInfo.setProvId(provId);
        
        this.populateLogginInfo(inputPayload);
                
        log.info("Processing Started");
        this.validateServiceOrderItem(inputPayload);
        
        try {
            return this.mobiProvisioner(inputPayload,resp);
        } finally {
            this.clearLoggingInfo();
        }
    }

    private void populateLogginInfo(MobitvReq inputPayload) {
        Boolean isValidationReq = reqInfo.getIsValidationReq();
        if (isValidationReq == null) {
            log.error("reqInfo needs to be set before populating logging info");
            throw new ServerInternalError("reqInfo needs to be set before populating logging info");
        }
        Integer serviceOrder = inputPayload.getServiceOrder();
     
        MDC.put("so", serviceOrder.toString());
        MDC.put("SimMode", isValidationReq.toString());
    }

    private void clearLoggingInfo() {
        MDC.remove("so");
        MDC.remove("SimMode");
    }

    private void updateDtf(MobitvReq req,ResponseEntity<MobiResponse> response){       
        
        //If its a simulation request, dont make dtf entry
        
        Boolean isvalidationReq = reqInfo.getIsValidationReq();
        if(isvalidationReq){
            log.debug("DTF is not updated for validation requests");
            return;
        }
        
        Long currentTime = System.currentTimeMillis();
        
        MobiResponse mobiRespRecieved = response.getBody();
        Purchase_response[] purResps = mobiRespRecieved.getPurchase_response();        
        for(Purchase_response purResp:purResps){
            DailyTransFile dtf = new DailyTransFile();
            dtf.setTxnTime(currentTime);  
            populateMasterDtfFromReq(dtf,req);
            populateDtfFromPurchase(dtf,req,purResp);
            dtfRepo.save(dtf);
            log.trace("Dtf updated with:{}",dtf);
        }   
    }
    
    private void populateDtfFromPurchase(DailyTransFile dtf,MobitvReq req,Purchase_response purResp){
        String prodId=purResp.getProduct_id();
        Long purchaseId = purResp.getPurchase_id();
        
        String status = purResp.getStatus();
        log.debug("prodId:{} purchaseId:{} status:{}",prodId,purchaseId,status);
        
        dtf.setTxnId(purchaseId);
        dtf.setProductId(prodId);
        dtf.setTxnType(status);
    }

    private String getOrigin(MobitvReq req){
        String action = req.getPurchase()[0].getAction();
        String origin = null;
        if(action.equals(WhatToDoWithComp.CREATE.name().toLowerCase())){
            origin = req.getPurchase()[0].getPurchase_origin();    
        }else{
            origin = req.getPurchase()[0].getCancel_origin();
        }
        return origin;
    }
    
   
    private void populateMasterDtfFromReq(DailyTransFile dtf,MobitvReq req){
        //Get Origin
        dtf.setOrigin(this.getOrigin(req));
        
        dtf.setBsiOmniaSo(((Number)(req.getServiceOrder())).longValue());

        //Get provId from reqInfo
        dtf.setProvId(reqInfo.getProvId());
        
        dtf.setuId(this.getExternalId(req));
        return;
    }
    private String getExternalId(MobitvReq req){
        
        String accId = req.getAccountCode();
        Long locId = req.getLocationId();
        if(locId==null){
            return accId;
        }else{
            return accId.toString()+"-"+locId.toString();
        }
    }
    private ProvMngrResponse mobiProvisioner(MobitvReq req,HttpServletResponse resp) {
        try {
            ResponseEntity<MobiResponse> response = processMobiRequest.processMobiRequest(req);
            if(response.getStatusCode()!=HttpStatus.OK){
                MobiResponse respRecieved = response.getBody();
                            
                String msgFailure = "Mobi Processing Failed. Error Code:"+respRecieved.getError_code()+
                " Error Msg:"+respRecieved.getError_message()+
                " Error Detail:"+respRecieved.getError_detail();
                
                InvalidRequest invalidReq = new InvalidRequest( msgFailure);
                mobiHouseKeepingSer.houseKeepingUpdate(req, invalidReq,
                        HouseKeepingErrorCodes.MOBI_PROCESSING_FAILED, 
                        HouseKeepingStatusCodes.FAILED,reqInfo.getProvId());
                resp.sendError(response.getStatusCode().value(),msgFailure);
                return new  ProvMngrResponse(utils.getCurrentEpoch(), response.getStatusCode().value(), msgFailure, null, 
                        msgFailure, ProvMngrResponse.MOBI,
                        false) ; 
                }else{
                    
                    //Update the DTF
                    this.updateDtf(req, response);
                }
        } catch (ResourceAccessException e) {
           log.error("Mobi PM- Mobi Server is down",e);
           mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.MOBI_DOWN, HouseKeepingStatusCodes.FAILED,reqInfo.getProvId());
           resp.setStatus(HttpStatus.NOT_FOUND.value());           
           return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.NOT_FOUND.value(), ProvMngrResponse.MOBI_PROCESSING_FAILED, utils.exceptionStackTrace(e), "Mobi PM- Mobi Server is down", ProvMngrResponse.MOBI,
                   false) ;
        } catch (PersistenceException e) {
            log.error("Processing failed with exception",e);
            mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.DB_UPDATED_FAILED, HouseKeepingStatusCodes.FAILED,reqInfo.getProvId());
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());           
            return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), utils.exceptionStackTrace(e), "Mobi PM- PersistenceException:DB operation failed", ProvMngrResponse.MOBI,
                    false) ;
        }catch (InvalidRequest e) {
           mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.INVALID_REQUEST_OR_CONFIG, HouseKeepingStatusCodes.FAILED,reqInfo.getProvId());
           resp.setStatus(HttpStatus.BAD_REQUEST.value());           
           return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.BAD_REQUEST.value(), ProvMngrResponse.MOBI_PROCESSING_FAILED, utils.exceptionStackTrace(e), "Mobi PM-"+e.getMessage(), ProvMngrResponse.MOBI,
                   false) ;
        } catch (InvalidConfig e) {
           mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.INTENAL_SERVER_ERROR, HouseKeepingStatusCodes.FAILED,reqInfo.getProvId());
           resp.setStatus(HttpStatus.BAD_REQUEST.value());           
           return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.BAD_REQUEST.value(), ProvMngrResponse.MOBI_PROCESSING_FAILED, utils.exceptionStackTrace(e), "Mobi PM-"+e.getMessage(), ProvMngrResponse.MOBI,
                   false) ;
        }catch (Exception e) {
            log.error("Processing failed with exception",  e);
            mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.INVALID_REQUEST_OR_CONFIG, HouseKeepingStatusCodes.FAILED,reqInfo.getProvId());
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "Exception doesn't have the error message. Please refer to the complete exception";
            }
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); 
            return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMsg, utils.exceptionStackTrace(e), "Mobi PM-"+errorMsg, ProvMngrResponse.MOBI,
                    false) ;           
        }        
        return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.OK.value(), null, null, "Mobi provisioning successful", ProvMngrResponse.MOBI,
                true) ;  
    }

}
