package com.cspire.prov.mobi.xml.req;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cspire.prov.dtf.model.IptvFipsCode;
import com.cspire.prov.dtf.model.IptvFipsCodeRepo;
import com.cspire.prov.fips.PrepareFips;
import com.cspire.prov.framework.apmax.payload.jaxb.REQUEST;
import com.cspire.prov.framework.apmax.payload.jaxb.REQUEST.SERVICE.ITEM.FEATURE;
import com.cspire.prov.framework.apmax.payload.jaxb.REQUEST.SERVICE.ITEM.QUANTITYBASED.COMPONENT;
import com.cspire.prov.framework.exceptions.InvalidConfig;
import com.cspire.prov.framework.global.constants.Defaults;
import com.cspire.prov.framework.global.constants.GlobalEnums;
import com.cspire.prov.framework.model.RawXmlStringPayload;
import com.cspire.prov.framework.model.mobi.Extended_property;
import com.cspire.prov.framework.model.mobi.MobitvReq;
import com.cspire.prov.framework.model.mobi.Purchase;
import com.cspire.prov.framework.model.mobi.WhatToDoWithComp;
import com.cspire.prov.framework.xml.processor.XmlReqToObjProcessor;

@Component
public class OmniaPayloadAdaptor {

    private static final Logger log = LoggerFactory.getLogger(OmniaPayloadAdaptor.class);

    @Autowired
    private IptvFipsCodeRepo iptvFipsCodeRepo;
    
    @Autowired
    XmlReqToObjProcessor mobiPayloadProcessor;
    
    @Value("${mobi.config.stream.code}")
    String streamCode;
    
    @Value("${mobi.config.dvr.code}")
    String dvrCode;
    
    public MobitvReq omniaXmlToMobiReq(RawXmlStringPayload xmlRequest) throws IOException{
        return omniaXmlToMobiReq(xmlRequest, false);
    }
    public MobitvReq omniaXmlToMobiReq(RawXmlStringPayload xmlRequest,Boolean isSimulate) throws IOException{
        String xmlReq = xmlRequest.getXmlString();
        
        
        MobitvReq mobitvReq = new MobitvReq();
        mobitvReq.setServiceRequestItemId(xmlRequest.getServiceRequestItemId());
        REQUEST req = (REQUEST) mobiPayloadProcessor.xmlStringToObject(xmlReq);
        mobitvReq.setAccountCode(req.getACCOUNT().getACCOUNTCODE().trim());
        mobitvReq.setServiceOrder(req.getSERVICEORDERNUMBER());
        mobitvReq.setIsValidationReq(isSimulate);
        mobitvReq.setPurchase(this.getIptvChannelList(req));
        mobitvReq.setOrigin(Defaults.ORIGIN_OMNIA);
        
        IptvFipsCode fips = this.getIptvFipsCode(req);
        String county = fips.getCounty();
        String state = fips.getState();
        
        mobitvReq.setFipsCode(
        PrepareFips.prepareFips(state, county));
        
        
         return mobitvReq;        
    }
    
	private IptvFipsCode getIptvFipsCode(REQUEST req) {

        String countyJurisdiction = req.getSERVICE().getITEM().getLOCATION().getCOUNTYJURISDICTION();
        Integer serviceOrder = req.getSERVICEORDERNUMBER();

        List<IptvFipsCode> iptvFipsCodes = iptvFipsCodeRepo.findByFipsKey(countyJurisdiction);

        if (iptvFipsCodes.size() > 1) {
            log.warn("SO:{} processing stopped as more than one iptvFipsCodes found for countyJurisdiction:{}",
                    serviceOrder, countyJurisdiction);
            throw new InvalidConfig("More than one iptvFipsCodes found for countyJurisdiction:" + countyJurisdiction);
        } else if (iptvFipsCodes.size() == 0) {
            log.warn("SO:{} processing stopped as No iptvFipsCodes found for countyJurisdiction: {}", serviceOrder,
                    countyJurisdiction);
            throw new InvalidConfig("No iptvFipsCodes found for CountyJurisdiction: " + countyJurisdiction);
        }
        log.debug("SO:{} The iptvFipsCodes found is {}", serviceOrder, iptvFipsCodes.get(0).toString());
        return iptvFipsCodes.get(0);
    }
	
	
    private Integer getDvrQuantity(REQUEST req){
    	Integer qnt = getCompQuantity( req,  this.dvrCode);
    	if(qnt!=null){
        	log.trace("{} Dvr={}",dvrCode,qnt);
    	}
    	return qnt;
    	
    }
    
    private Integer getStreamQuantity(REQUEST req){
    	Integer qnt =  getCompQuantity( req,  this.streamCode);
    	if(qnt!=null){
        	log.trace("{} Stream={}",streamCode,qnt);
    	}
    	return qnt;
    }
    
    private Integer getCompQuantity(REQUEST req, String inputCompCode){
    	List<COMPONENT> comps = req.getSERVICE().getITEM().getQUANTITYBASED().getCOMPONENT();
    	for(COMPONENT comp:comps){
    		
    		//String compCode = comp.getACTIVATIONDATE();
    		String compCode = comp.getCOMPONENTCODE();
    		if(compCode.equals(inputCompCode)){
    			return (int) comp.getQUANTITY();
    		}
    	}
    	return null;
    }
    private Purchase[] getIptvChannelList(REQUEST req) {
        List<FEATURE> featureList = req.getSERVICE().getITEM().getFEATURE();
        ArrayList<Purchase> purchaseList = new ArrayList<Purchase>();
       
        for (FEATURE feature : featureList) {
            WhatToDoWithComp action = this.whatToDoWithComponent(feature);
            switch(action){
            case CREATE:
            case CANCEL:
                Purchase purchase=this.createMobiPurchase(feature, action);
                purchaseList.add(purchase);
                break;
            case IGNORE:
                break;
            default:
            throw new InvalidConfig("Unknown action for component code");            
            }           
        }
        
        Purchase purchase=getDvrQtyPurchase(req);
        if(null != purchase){
            purchaseList.add(purchase);
        }
        
        purchase=getStreamQtyPurchase(req);
        if(null != purchase){
            purchaseList.add(purchase);
        }
        
        Purchase[] purchaseArray = new Purchase[purchaseList.size()];        
        return purchaseList.toArray(purchaseArray);
    }
    
    private Purchase getDvrQtyPurchase(REQUEST req){
        Integer dvrQty = this.getDvrQuantity(req);
        Purchase purchase = null;
        if(null != dvrQty){
        	purchase=this.createQtyPurchase(this.dvrCode,dvrQty.toString());
        }
        return purchase;
    }
    
    private Purchase getStreamQtyPurchase(REQUEST req){
        Integer strmQty = this.getStreamQuantity(req);
        Purchase purchase = null;
        if(null != strmQty){
        	purchase=this.createQtyPurchase(this.streamCode,strmQty.toString());
        }
        return purchase;
    }
    
    private Purchase createQtyPurchase(String comp,String qty){
    	Purchase purchase = new Purchase();
        purchase.setProduct_id(comp);
        purchase.setAction(GlobalEnums.CREATE.name().toLowerCase());
        
        Extended_property[] extPropArray = new Extended_property[1];
        purchase.setExtended_property(extPropArray);
        
        extPropArray[0] = new Extended_property();
        extPropArray[0].setName(GlobalEnums.QUANTITY.name().toLowerCase());
        extPropArray[0].setValue(qty);
        return purchase;   	
    }
    
    private Purchase createMobiPurchase(FEATURE feature,WhatToDoWithComp action){
        Purchase purchase = new Purchase();
        purchase.setProduct_id(feature.getCOMPONENTCODE().trim());
        purchase.setAction(action.name().trim().toLowerCase());
        return purchase;
    }
    
    private WhatToDoWithComp whatToDoWithComponent(FEATURE feature){
        
            if(feature.getACTION().equals("D")){
                log.info("ACTION:{}, Hence COMPONENTCODE:{} would be deleted from mobi", 
                        feature.getACTION(),feature.getCOMPONENTCODE());
                return WhatToDoWithComp.CANCEL;
            } else if(feature.getACTION().equals("N")){
                log.info("ACTION:{}, COMPONENTCODE:{} would be added in mobi", 
                        feature.getACTION(),feature.getCOMPONENTCODE());
                return WhatToDoWithComp.CREATE;
            }else if(feature.getACTION().equals("X")){
                log.info("ACTION:{}, COMPONENTCODE:{} would be ignored", 
                        feature.getACTION(),feature.getCOMPONENTCODE());
                return WhatToDoWithComp.IGNORE;
            }else{
                log.info("ACTION:{}, COMPONENTCODE:{} Unkonk Action.", 
                        feature.getACTION(),feature.getCOMPONENTCODE());
                throw new InvalidConfig("Unknown Action"+feature.getACTION());
            }
        
    }
}
