package com.brm.rest.services.dao;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.customer.put.Adjustment;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.*;
import com.portal.pcm.fields.FldAmount;
import com.portal.pcm.fields.FldDescr;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldProgramName;

public class AdjustmentDao {
	private static final Logger log = Logger.getLogger("Connector");
	
	public Response postAdjustment(Adjustment adjustment) {
		Response response = new Response();
		
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			Poid accountPoid = connector.getAccountPoidFromAccountNo(adjustment.getAccountNo());
			FList adjFlist = new FList();
			adjFlist.set(FldPoid.getInst(), accountPoid);
			String reasonString;
			if(adjustment.getMode().equalsIgnoreCase("CREDIT")) {
				adjFlist.set(FldAmount.getInst(), new BigDecimal(adjustment.getAmount() * -1));
				reasonString = "Reason Codes-Credit Reasons";
			}
			else {
				adjFlist.set(FldAmount.getInst(), new BigDecimal(adjustment.getAmount()));
				reasonString = "Reason Codes-Debit Reasons";
			}
			adjFlist.set(FldProgramName.getInst(), "SFDC - Adjustment");
			adjFlist.set(FldDescr.getInst(), "["+adjustment.getDescription()+"]");
			log.info("the adjustment input " +adjFlist);
			
			response.setResponseCode(0);
			response.setResponseMsg("SUCCESS");
				
		}  catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			response.setResponseMsg(ex.getMessage());
			response.setResponseCode(ex.getErrorCode());
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			response.setResponseMsg(ex.getMessage());
			response.setResponseCode(1);
			
		} finally {
			log.info("Connector completes processing for postAdjustment");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		
	    	}
		}
		return response;
	}
	
	private FList getVersionAndID(String reasonString, String description) {
		FList returnFlist = new FList();
		
		return returnFlist;
	}
	
}
