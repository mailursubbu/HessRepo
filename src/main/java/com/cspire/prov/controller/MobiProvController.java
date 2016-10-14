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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.cspire.prov.framework.blackout.BlackoutService;
import com.cspire.prov.framework.exceptions.InvalidConfig;
import com.cspire.prov.framework.exceptions.InvalidRequest;
import com.cspire.prov.framework.exceptions.ServerInternalError;
import com.cspire.prov.framework.housekeeping.HouseKeepingErrorCodes;
import com.cspire.prov.framework.housekeeping.HouseKeepingService;
import com.cspire.prov.framework.housekeeping.HouseKeepingStatusCodes;
import com.cspire.prov.framework.model.ProvMngrResponse;
import com.cspire.prov.framework.model.RawXmlStringPayload;
import com.cspire.prov.framework.model.mobi.MobiResponse;
import com.cspire.prov.framework.model.mobi.MobitvReq;
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
    
    @CrossOrigin
    @RequestMapping(value = "/mobi/omnia", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse mobiForOmniaProcessor(@RequestBody RawXmlStringPayload xmlRequest,HttpServletResponse resp) throws IOException {
        if (blackoutService.checkForBalckout("rest/mobi/omnia")) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.BLACKOUT, null, null,
                    ProvMngrResponse.FAIL_MSG, ProvMngrResponse.BLACKOUT_MSG, true);
        }              
        MobitvReq inputPayload=omniaPayloadAdaptor.omniaXmlToMobiReq(xmlRequest);        
        return receiveReqAndSetLoginfo(inputPayload, false,inputPayload.getServiceRequestItemId(),resp);
    }
    
    @CrossOrigin
    @RequestMapping(value = "/mobi", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse mobiProvRequestProcessor(@RequestBody MobitvReq inputPayload,HttpServletResponse resp) {
        // TODO: Implement the filter based blackout.
        if (blackoutService.checkForBalckout("rest/mobi")) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.BLACKOUT, null, null,
                    ProvMngrResponse.FAIL_MSG, ProvMngrResponse.BLACKOUT_MSG, true);
        }
        // Try the readonly operations
        return receiveReqAndSetLoginfo(inputPayload, false,inputPayload.getServiceRequestItemId(),resp);
    }


    @CrossOrigin
    @RequestMapping(value = "/mobiSimulate", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse mobiProvRequestValidator(@RequestBody MobitvReq inputPayload,HttpServletResponse resp) {
        // TODO: Implement the filter based blackout.
        if (blackoutService.checkForBalckout("rest/mobiSimulate")) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.BLACKOUT, null, null,
                    ProvMngrResponse.FAIL_MSG, ProvMngrResponse.BLACKOUT_MSG, true);
        }

        /*
         * Lets use orig req for all the read only txns with ApMax. maskedReq
         * would be used for all the write txns with ApMax. House Keeping would
         * be with orig request parameters.
         */
        return receiveReqAndSetLoginfo(inputPayload, true,inputPayload.getServiceRequestItemId(),resp);
    }
    
    private void validateServiceOrderItem(MobitvReq inputPayload){
        Integer serviceRequestItemId = inputPayload.getServiceRequestItemId();
        if(serviceRequestItemId==null){
            log.error("Input payload is missing serviceRequestItemId field");
            throw new InvalidRequest("Input payload is missing serviceRequestItemId field");            
        }
    }
    
    private ProvMngrResponse receiveReqAndSetLoginfo(MobitvReq inputPayload, Boolean isValidateReq,Integer serviceRequestItemId,HttpServletResponse resp) {       
        reqInfo.setIsValidationReq(isValidateReq);        
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

    private ProvMngrResponse mobiProvisioner(MobitvReq req,HttpServletResponse resp) {
        try {
            log.debug("test");
            ResponseEntity<MobiResponse> response = processMobiRequest.processMobiRequest(req);
            if(response.getStatusCode()!=HttpStatus.OK){
                resp.sendError(response.getStatusCode().value(),response.getBody().getMessage());
                throw new InvalidRequest("Mobi provisioning failed");
            }
        } catch (ResourceAccessException e) {
           mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.MOBI_DOWN, HouseKeepingStatusCodes.FAILED);
           resp.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());           
           return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.SERVICE_UNAVAILABLE.value(), ProvMngrResponse.MOBI_PROCESSING_FAILED, utils.exceptionStackTrace(e), e.getMessage(), ProvMngrResponse.MOBI,
                   false) ;
        } catch (PersistenceException e) {
            log.error("Processing failed with exception",e);
            mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.DB_UPDATED_FAILED, HouseKeepingStatusCodes.FAILED);
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());           
            return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), utils.exceptionStackTrace(e), e.getMessage(), ProvMngrResponse.MOBI,
                    false) ;
        } catch (HttpClientErrorException e) {
            log.error("Processing failed with exception", e);
            mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.MOBI_PROCESSING_FAILED, HouseKeepingStatusCodes.FAILED);            
            resp.setStatus(e.getStatusCode().value());
            return new  ProvMngrResponse(utils.getCurrentEpoch(), e.getStatusCode().value(), ProvMngrResponse.MOBI_PROCESSING_FAILED, utils.exceptionStackTrace(e), e.getMessage(), ProvMngrResponse.MOBI,
                    false) ;            
        }catch (InvalidConfig e) {
           mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.INTENAL_SERVER_ERROR, HouseKeepingStatusCodes.FAILED);
           resp.setStatus(HttpStatus.BAD_REQUEST.value());           
           return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.BAD_REQUEST.value(), ProvMngrResponse.MOBI_PROCESSING_FAILED, utils.exceptionStackTrace(e), e.getMessage(), ProvMngrResponse.MOBI,
                   false) ;
        }catch (Exception e) {
            log.error("Processing failed with exception",  e);
            mobiHouseKeepingSer.houseKeepingUpdate(req, e,
                    HouseKeepingErrorCodes.INVALID_REQUEST_OR_CONFIG, HouseKeepingStatusCodes.FAILED);
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "Exception doesn't have the error message. Please refer to the complete exception";
            }
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); 
            return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMsg, utils.exceptionStackTrace(e), errorMsg, ProvMngrResponse.MOBI,
                    false) ;           
        }        
        return new  ProvMngrResponse(utils.getCurrentEpoch(), HttpStatus.OK.value(), null, null, null, ProvMngrResponse.MOBI,
                true) ;  
    }

}
