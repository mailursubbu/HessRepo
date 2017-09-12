package com.brm.rest.services.dao;

import java.util.Enumeration;
import org.apache.log4j.Logger;
import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.aoc.AocRequest;
import com.brm.service.portal.bean.aoc.AocResponse;
import com.brm.service.portal.bean.aoc.ChargeDetails;
import com.brm.service.portal.bean.aoc.Products;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.*;
import com.portal.pcm.fields.*;

public class AdviceOfChargeDao {
	
	private static final Logger log = Logger.getLogger("Connector");
	
	public AocResponse getAdviceOfCharge(AocRequest request) {
		
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			FList inputFlist = new FList();
			inputFlist.set(FldName.getInst(), request.getPlanName());
			inputFlist.set(FldPoid.getInst(), new Poid(1, 1, "/account"));
			int recId = 0;
			for(Products prod : request.getProdInfo()) {
				log.info("the name" +prod.getName());
				log.info("the charge" +prod.getCharge());
				log.info("the quantity" +prod.getQuantity());
				FList prodFlist = new FList();
				prodFlist.set(FldName.getInst(), prod.getName());
				inputFlist.setElement(FldProducts.getInst(), recId++, prodFlist);
			}
			log.info("the flist is" +inputFlist);
			FList outFlist = connector.callOpcode(1000001, inputFlist, "getAdviceOfCharge");
			log.info("the flist is" +outFlist);
			AocResponse response = new AocResponse();
			if(outFlist.containsKey(FldName.getInst())) {
				response.setPlanName(outFlist.get(FldName.getInst()));
				response.setTotalCharge(outFlist.get(FldAmount.getInst()).doubleValue());
				response.setTotalDiscount(outFlist.get(FldDiscount.getInst()).doubleValue());
				response.setTotalTax(outFlist.get(FldTax.getInst()).doubleValue());				
			}
			if(outFlist.containsKey(FldProducts.getInst())) {
				SparseArray productsArray = outFlist.get(FldProducts.getInst());
				Enumeration <?> products = productsArray.getValueEnumerator();
				while(products.hasMoreElements()) {
					FList productsFlist = (FList)products.nextElement();
					ChargeDetails charge = new ChargeDetails();
					charge.setName(productsFlist.get(FldName.getInst()));
					charge.setCharge(productsFlist.get(FldAmount.getInst()).doubleValue());
					charge.setDiscount(productsFlist.get(FldDiscount.getInst()).doubleValue());
					charge.setTax(productsFlist.get(FldTax.getInst()).doubleValue());
					response.addChargeDetails(charge);
				}
				
			}
			return response;
			
		}catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			
		} finally {
			log.info("Connector completes processing for getAdviceOfCharge");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		
	    	}
		
		}
		return null;
		
	}

}
