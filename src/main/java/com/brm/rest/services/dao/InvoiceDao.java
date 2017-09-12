package com.brm.rest.services.dao;

import java.util.Enumeration;
import org.apache.log4j.Logger;
import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.customer.get.InvoiceInfo;
import com.brm.service.portal.bean.customer.get.InvoiceList;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.*;
import com.portal.pcm.fields.*;

public class InvoiceDao {
	
	private static final Logger log = Logger.getLogger("Connector");
	/*public List<InvoiceInfo> getInvoiceDetails(String accountNo, int count) {
		List<InvoiceInfo> invoiceList = new ArrayList<InvoiceInfo>();
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			Poid accountPoid = connector.getAccountPoidFromAccountNo(accountNo);
			FList billFlist = getAccountBills(connector, accountPoid, count);
			log.info("the output flist" +billFlist);
			if(billFlist.containsKey(FldResults.getInst())) {
				SparseArray billArray = billFlist.get(FldResults.getInst());
				Enumeration <?> bills = billArray.getValueEnumerator();
				while(bills.hasMoreElements()) {
					FList invFlist = (FList)bills.nextElement();
					InvoiceInfo invInfo = new InvoiceInfo();
					invInfo.setBillNo(invFlist.get(FldBillNo.getInst()));
					invInfo.setDue(invFlist.get(FldDue.getInst()).doubleValue());
					invInfo.setCurrentTotal(invFlist.get(FldCurrentTotal.getInst()).doubleValue());
					invInfo.setStartDate(invFlist.get(FldStartT.getInst()));
					invInfo.setEndDate(invFlist.get(FldEndT.getInst()));
					invInfo.setDueDate(invFlist.get(FldDueT.getInst()));
					invoiceList.add(invInfo);
				}
			}
			
		}catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			
		} finally {
			log.info("Connector completes processing for getInvoiceDetails");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		
	    	}
		}
		
		return invoiceList;
	}*/
	
	public InvoiceList getInvoiceDetails(String accountNo, int count) {
		InvoiceList invoiceList = new InvoiceList();
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			Poid accountPoid = connector.getAccountPoidFromAccountNo(accountNo);
			invoiceList.setAccountPoid(""+accountPoid.getId());
			FList billFlist = getAccountBills(connector, accountPoid, count);
			log.info("the output flist" +billFlist);
			if(billFlist.containsKey(FldResults.getInst())) {
				SparseArray billArray = billFlist.get(FldResults.getInst());
				Enumeration <?> bills = billArray.getValueEnumerator();
				while(bills.hasMoreElements()) {
					FList invFlist = (FList)bills.nextElement();
					InvoiceInfo invInfo = new InvoiceInfo();
					invInfo.setBillNo(invFlist.get(FldBillNo.getInst()));
					invInfo.setBilledAmount(invFlist.get(FldTotals.getInst()).doubleValue());
					invInfo.setDueAmount(invFlist.get(FldDue.getInst()).doubleValue());
					invInfo.setBillDate(invFlist.get(FldEndT.getInst()));
					invInfo.setDueDate(invFlist.get(FldDueT.getInst()));
					invoiceList.addInvoices(invInfo);
				}
			}
			
		}catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			invoiceList.setSuccess(false);
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			invoiceList.setSuccess(false);
			
		} finally {
			log.info("Connector completes processing for getInvoiceDetails");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		invoiceList.setSuccess(false);
	    	}
		}
		
		return invoiceList;
	}
	
	private FList getAccountBills(PCMWrapper connector, Poid accountPoid, int count) throws BRMException {
		FList inputFlist = new FList();
		inputFlist.set(FldPoid.getInst(), accountPoid);
		inputFlist.set(FldStatus.getInst(), PortalConst.ITEM_STATUS_OPEN + PortalConst.ITEM_STATUS_CLOSED);
		inputFlist.set(FldIncludeChildren.getInst(), 0);
		inputFlist.set(FldNumberOfBills.getInst(), count);
		
		log.info("the input flist" +inputFlist);
		return connector.callOpcode(PortalOp.AR_GET_ACCT_BILLS, inputFlist, "getAccountBills");

	}

}
