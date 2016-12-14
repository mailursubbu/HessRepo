package com.cspire.prov.mobi.xml.req;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cspire.prov.framework.apmax.payload.jaxb.REQUEST;
import com.cspire.prov.framework.apmax.payload.jaxb.REQUEST.SERVICE.ITEM.FEATURE;
import com.cspire.prov.framework.apmax.payload.jaxb.REQUEST.SERVICE.ITEM.QUANTITYBASED.COMPONENT;
import com.cspire.prov.framework.exceptions.InvalidConfig;
import com.cspire.prov.framework.global.constants.Defaults;
import com.cspire.prov.framework.global.constants.StreamDvrCompName;
import com.cspire.prov.framework.model.RawXmlStringPayload;
import com.cspire.prov.framework.model.mobi.MobitvReq;
import com.cspire.prov.framework.model.mobi.Purchase;
import com.cspire.prov.framework.model.mobi.WhatToDoWithComp;
import com.cspire.prov.framework.xml.processor.XmlReqToObjProcessor;

@Component
public class OmniaPayloadAdaptor {

    private static final Logger log = LoggerFactory.getLogger(OmniaPayloadAdaptor.class);

    @Autowired
    XmlReqToObjProcessor mobiPayloadProcessor;
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
/*        mobitvReq.setDvrQuantity(this.getDvrQuantity(req));
        mobitvReq.setStreamQuantity(this.getStreamQuantity(req));*/
        
        return mobitvReq;        
    }
    
    private Integer getDvrQuantity(REQUEST req){
    	Integer qnt = getCompQuantity( req,  StreamDvrCompName.TV2STOR.name());
    	if(qnt!=null){
        	log.trace("TV2STOR Dvr={}",qnt);
    	}
    	return qnt;
    	
    }
    
    private Integer getStreamQuantity(REQUEST req){
    	Integer qnt =  getCompQuantity( req,  StreamDvrCompName.TV2STRM.name());
    	if(qnt!=null){
        	log.trace("TV2STRM Stream={}",qnt);
    	}
    	return qnt;
    }
    
    private Integer getCompQuantity(REQUEST req, String inputCompCode){
    	List<COMPONENT> comps = req.getSERVICE().getITEM().getQUANTITYBASED().getCOMPONENT();
    	for(COMPONENT comp:comps){
    		String compCode = comp.getACTIVATIONDATE();
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
        
        Purchase[] purchaseArray = new Purchase[purchaseList.size()];        
        return purchaseList.toArray(purchaseArray);
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
