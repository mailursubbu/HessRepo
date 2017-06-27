package com.cspire.prov.mobi.xml.req;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.cspire.prov.framework.global.constants.MobiAccStatus;
import com.cspire.prov.framework.housekeeping.HouseKeepingErrorCodes;
import com.cspire.prov.framework.housekeeping.HouseKeepingStatusCodes;
import com.cspire.prov.framework.model.RawXmlStringPayload;
import com.cspire.prov.framework.model.mobi.Extended_property;
import com.cspire.prov.framework.model.mobi.MobitvReq;
import com.cspire.prov.framework.model.mobi.Purchase;
import com.cspire.prov.framework.model.mobi.WhatToDoWithComp;
import com.cspire.prov.framework.utils.UtilFuncs;
import com.cspire.prov.framework.xml.processor.XmlReqToObjProcessor;
import com.cspire.prov.housekeeping.MobiHouseKeepingService;

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
	
	@Autowired
	MobiHouseKeepingService mobiHouseKeepingSer;
	

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

		IptvFipsCode fips = null;
		try{
			fips = this.getIptvFipsCode(req);	
		}catch(InvalidConfig e){
			//Make an house keeping entry about this failure
			mobiHouseKeepingSer.houseKeepingUpdate(mobitvReq, e,
					HouseKeepingErrorCodes.MOBI_PROCESSING_FAILED, 
					HouseKeepingStatusCodes.FAILED,Defaults.DEFAULT_PROV_ID);
			throw e;
		}
		
		String county = fips.getCounty();
		String state = fips.getState();

		mobitvReq.setFipsCode(
				PrepareFips.prepareFips(state, county));

		mobitvReq.setStatus(
				getMobiAccStatus(req).getStrVal());

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
				if(comp.getACTION().equals("X") &&
						!isDisconnectOperation(req) &&
						!isSuspendOperation(req) && 
						!isUnsuspendReconnectOp(req)){
					log.trace("{} is X , hence quantity would be returned as null",compCode);
					return null;
				}
				if(isSuspendOperation(req) ||
						isUnsuspendReconnectOp(req) ||
						isDisconnectOperation(req) ){
					log.info("As activity is set to \"S\" OR \"SR\" OR \"D\", feature code with X would also be considered for cancellation");
				}
				return (int) comp.getQUANTITY();
			}
		}
		return null;
	}

	private Purchase[] getIptvChannelList(REQUEST req) {
		List<FEATURE> featureList = req.getSERVICE().getITEM().getFEATURE();
		ArrayList<Purchase> purchaseList = new ArrayList<Purchase>();

		MobiAccStatus accStatus = getMobiAccStatus(req);

		for (FEATURE feature : featureList) {
			WhatToDoWithComp action = this.whatToDoWithComponent(req,feature);
			switch(action){
			case CREATE:
			case CANCEL:
				Purchase purchase=this.createMobiPurchase(feature, action,accStatus);
				purchaseList.add(purchase);
				break;
			case IGNORE:
				break;
			default:
				throw new InvalidConfig("Unknown action for component code");            
			}           
		}

		Purchase purchase=null;
		//For suspend and resume, DVR wont be changed.
		if(!isSuspendOperation(req) &&
				!isUnsuspendOperation(req)){
			purchase=getDvrQtyPurchase(req,accStatus);
			if(null != purchase){
				purchaseList.add(purchase);
			}	
		}else{
			log.info("Suspend/Unsuspend transation, DVR qty wont be updated");
		}


		purchase=getStreamQtyPurchase(req,accStatus);
		if(null != purchase){
			purchaseList.add(purchase);
		}

		Purchase[] purchaseArray = new Purchase[purchaseList.size()];        
		return purchaseList.toArray(purchaseArray);
	}

	private Boolean isSuspendOperation(REQUEST req){
		String operation = req.getSERVICE().getACTIVITY();
		if(operation.equals("S")
				|| operation.equals("NS")
				|| operation.equals("ND")
				){
			return true;
		}else{
			return false;
		}
	}
	
	private Boolean isUnsuspendReconnectOp(REQUEST req){
		if(isUnsuspendOperation(req) 
				|| isReconnectOperation(req)
				){
			return true;
		}else{
			return false;
		}
	}
	
	private Boolean isUnsuspendOperation(REQUEST req){
		String operation = req.getSERVICE().getACTIVITY();
		if(operation.equals("SR")
				|| operation.equals("P")
				|| operation.equals("NR")
				){
			return true;
		}else{
			return false;
		}
	}
	
	private Boolean isReconnectOperation(REQUEST req){
		String operation = req.getSERVICE().getACTIVITY();
		if(operation.equals("R")){
			return true;
		}else{
			return false;
		}
	}
	
	private Boolean isDisconnectOperation(REQUEST req){
		String operation = req.getSERVICE().getACTIVITY();
		if(operation.equals("D") ){
			return true;
		}else{
			return false;
		}
	}
	
	private Purchase getDvrQtyPurchase(REQUEST req,MobiAccStatus accStatus){
		Integer dvrQty = this.getDvrQuantity(req);
		Purchase purchase = null;
		if(null != dvrQty){
			purchase=this.createQtyPurchase(req,this.dvrCode,dvrQty.toString());
			purchase.setReason_code(accStatus.getStrVal());
		}
		return purchase;
	}

	private Purchase getStreamQtyPurchase(REQUEST req,MobiAccStatus accStatus){
		Integer strmQty = this.getStreamQuantity(req);

		Purchase purchase = null;
		if(null != strmQty){
			purchase=this.createQtyPurchase(req,this.streamCode,strmQty.toString());
			purchase.setReason_code(accStatus.getStrVal());
		}
		return purchase;
	}

	private Purchase createQtyPurchase(REQUEST req,String comp,String qty){
		Purchase purchase = new Purchase();
		purchase.setProduct_id(comp);
		GlobalEnums action = getActionForMobi(req);
		purchase.setAction(action.name().toLowerCase());			
		Extended_property[] extPropArray = new Extended_property[1];
		purchase.setExtended_property(extPropArray);
		extPropArray[0] = new Extended_property();
		extPropArray[0].setName(GlobalEnums.QUANTITY.name().toLowerCase());
		extPropArray[0].setValue(qty);
		return purchase;   	
	}

	private GlobalEnums getActionForMobi(REQUEST req){
		if(isDisconnectOperation(req) ||
				isSuspendOperation(req)){
			log.trace("Disconnect and Suspend operations needs cancel action for quantity fields");
			return GlobalEnums.CANCEL;
		}else{
			return GlobalEnums.CREATE;
		}		
	}
	private Purchase createMobiPurchase(FEATURE feature,WhatToDoWithComp action,MobiAccStatus reasonCode){
		Purchase purchase = new Purchase();
		purchase.setProduct_id(feature.getCOMPONENTCODE().trim());
		purchase.setAction(action.name().trim().toLowerCase());
		purchase.setReason_code(reasonCode.getStrVal());
		return purchase;
	}

	private WhatToDoWithComp whatToDoWithComponent(REQUEST req,FEATURE feature){

		String operation = req.getSERVICE().getACTIVITY();
		if(isSuspendOperation(req)
				|| isDisconnectOperation(req)
				){
			log.info("Service Actity is \"{}\", hence all the comp codes would be cancelled",operation );
			return WhatToDoWithComp.CANCEL;
		}
		
		if(isUnsuspendReconnectOp(req)){
			log.info("Service Actity is \"{}\", hence all the comp codes would be created",operation );
			return WhatToDoWithComp.CREATE;
		}
		
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

	public MobiAccStatus getMobiAccStatus(REQUEST req) {                
		String serActivity = req.getSERVICE().getACTIVITY();

		MobiAccStatus retVal = MobiAccStatus.DEFAULT;
		switch (serActivity) {
		case "A":
			log.trace("Processing new account request ");
			retVal = MobiAccStatus.DEFAULT;
			break;
		case "D":
		
			log.trace("Processing Terminate request ");
			retVal = MobiAccStatus.DISCONNECT;
			break;
		case "S":
		case "NS":
		case "ND":
			log.trace("Processing Suspend request ");
			retVal = MobiAccStatus.SUSPEND;
			break;
		case "R":
		case "NR":
		case "P":
		case "SR":
			log.trace("Processing reconnect request");
			retVal = MobiAccStatus.DEFAULT;
			break;
		case "C":
			log.trace("Processing change request");
			retVal = MobiAccStatus.DEFAULT;
			break;
		case "M":
			log.trace("Processing move request");
			retVal = MobiAccStatus.DEFAULT;
			break;    
		default:
			log.error("Unhandled service Activity");
			break;
			// throw new InvalidConfig("SO:"+serviceOrder+" Unhandled service
			// Activity:"+serActivity);
		}
		return retVal;
	}

	private Boolean isActiveComponent(FEATURE feature){

		String strDeactDate = feature.getDEACTIVATIONDATE();
		Date deactDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		if(strDeactDate!=null && !strDeactDate.equals("")){            
			deactDate = UtilFuncs.stringToDate(strDeactDate);
			Date todayDate = UtilFuncs.todayDate();
			if(deactDate.compareTo(todayDate)<=0){
				log.info("DEACTIVATIONDATE:{} <= today's date:{} for COMPONENTCODE:{}."
						+ "Hence it wont be provisioned", 
						feature.getDEACTIVATIONDATE(),formatter.format(todayDate),feature.getCOMPONENTCODE());
				return false;
			}else{
				log.info("DEACTIVATIONDATE:{} > today's date:{} for COMPONENTCODE:{}."
						+ "Hence component code would be used for provisioning", 
						feature.getDEACTIVATIONDATE(),formatter.format(todayDate),feature.getCOMPONENTCODE());
			}
		}
		return true;
	}

}
