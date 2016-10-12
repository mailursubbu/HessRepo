package com.cspire.prov.housekeeping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cspire.prov.framework.housekeeping.HouseKeepingErrorCodes;
import com.cspire.prov.framework.housekeeping.HouseKeepingNetworkElements;
import com.cspire.prov.framework.housekeeping.HouseKeepingService;
import com.cspire.prov.framework.housekeeping.HouseKeepingStatusCodes;
import com.cspire.prov.framework.model.mobi.MobitvReq;

public class MobiHouseKeepingService {
    private static final Logger log = LoggerFactory.getLogger(MobiHouseKeepingService.class);

    @Autowired
    HouseKeepingService houseKeepingService;
    
    public Boolean houseKeepingUpdate(MobitvReq req,Throwable exception,HouseKeepingErrorCodes errorCode,
            HouseKeepingStatusCodes statusCode){
      
        Integer serviceRequestItemId = req.getServiceRequestItemId();
        HouseKeepingNetworkElements ne=HouseKeepingNetworkElements.MOBI;
        if(req.getIsValidationReq()){
            ne=HouseKeepingNetworkElements.APMAX_VALIDATION;
        }
        
       Integer accountNum=null;       
       try{
           accountNum=Integer.parseInt(req.getAccountNum().trim());   
       }catch(Exception e){
           log.debug("Not able to convert String getACCOUNTCODE:"+
                        req.getAccountNum().trim()+
                        " into Integer. Lets not fail because of this. "
                        + "Lets use account id for housekeeping in this case",e);
           accountNum=Integer.parseInt(req.getAccountNum());
       }
       
       Integer serviceOrder=req.getServiceOrder();
       String type=req.getType();
       
       try{
           houseKeepingService.newRequestEntry(accountNum,serviceOrder,type,ne,
                   exception,errorCode.getErrorCode(),statusCode.getStatusCode(),serviceRequestItemId);
       }catch(Exception e){
           log.error("Housekeeping updated failed with exception",e);
           return false;
       }
       log.debug("Successfuly updated housekeeping table");
       return true;
    }    
}
