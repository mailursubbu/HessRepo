package com.brm.rest.services.dao;

import java.math.BigDecimal;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.customer.get.BalanceSummary;
import com.brm.service.portal.bean.customer.get.SummaryBal;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.FList;
import com.portal.pcm.Poid;
import com.portal.pcm.PortalOp;
import com.portal.pcm.SparseArray;
import com.portal.pcm.fields.FldBalances;
import com.portal.pcm.fields.FldCreditLimit;
import com.portal.pcm.fields.FldCurrentBal;
import com.portal.pcm.fields.FldFlags;
import com.portal.pcm.fields.FldPoid;

public class BalanceDao {
	
	private static final Logger log = Logger.getLogger("Connector");
	
	public BalanceSummary getBalance(String accountNo) {
		PCMWrapper connector = new PCMWrapper();
		BalanceSummary balSummary = new BalanceSummary();
		balSummary.setAccountNo(accountNo);
		Integer resourceId;
		try {
			connector.createContext();
			Poid accountPoid = connector.getAccountPoidFromAccountNo(accountNo);
			FList returnFlist = getBalanceFlist(connector, accountPoid, 0);
//			log.info(returnFlist);
			SparseArray balArray = returnFlist.get(FldBalances.getInst());
			Enumeration <?> balances = balArray.getKeyEnumerator();
			while(balances.hasMoreElements()) {
				SummaryBal oneBal = new SummaryBal();				
				resourceId = (Integer)balances.nextElement();
				oneBal.setResourceId(resourceId);
				oneBal.setCurrentBalance(balArray.elementAt(resourceId).get(FldCurrentBal.getInst()).doubleValue());
				balSummary.addBalList(oneBal);
			}
			
		}catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			
		} finally {
			log.info("Connector completes processing for ");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		
	    	}
		}
		return balSummary;
	}
	
	private FList getBalanceFlist(PCMWrapper connector, Poid accountPoid, int balType) throws BRMException {
		FList inputFlist = new FList();
		inputFlist.set(FldPoid.getInst(), accountPoid);
		inputFlist.set(FldFlags.getInst(), balType);
		FList balFlist = new FList();
		balFlist.set(FldCurrentBal.getInst(), new BigDecimal(0));
		balFlist.set(FldCreditLimit.getInst(), new BigDecimal(0));
		inputFlist.setElement(FldBalances.getInst(), -1, balFlist);
		
//		log.info("the input flist" +inputFlist);
		
		return connector.callOpcode(PortalOp.BAL_GET_BALANCES, inputFlist, "BalanceDao");
		
	}


}
