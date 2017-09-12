package com.brm.rest.services.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.constants.BrmConstants;
import com.brm.service.portal.bean.customer.get.PaymentInfo;
import com.brm.service.portal.bean.customer.get.PaymentList;
import com.brm.service.portal.bean.customer.put.Payment;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.*;
import com.portal.pcm.fields.*;
import org.apache.log4j.Logger;

public class PaymentDao {
	private static final Logger log = Logger.getLogger("Connector");
	
	public List<PaymentInfo> getPaymentDetails(String accountNo, int count) {
		List<PaymentInfo> paymentList = new ArrayList<PaymentInfo>();
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			Poid accountPoid = connector.getAccountPoidFromAccountNo(accountNo);
			String poidType = "/item/payment,/item/payment/reversal,/item/refund";
			FList paymentFlist = connector.getAccountPayments(accountPoid, count, 
					PortalConst.ITEM_STATUS_OPEN + PortalConst.ITEM_STATUS_CLOSED, poidType);
			log.info("the payment out flist: " +paymentFlist);
			
			if(paymentFlist.containsKey(FldResults.getInst())) {
				SparseArray payArray = paymentFlist.get(FldResults.getInst());
				Enumeration <?> payment = payArray.getValueEnumerator();
				while(payment.hasMoreElements()) {
					FList payFlist = (FList)payment.nextElement();
					PaymentInfo payInfo = new PaymentInfo();
					payInfo.setAmount(payFlist.get(FldItemTotal.getInst()).doubleValue());
					payInfo.setName(payFlist.get(FldName.getInst()));
					payInfo.setPaymentDate(payFlist.get(FldCreatedT.getInst()));
					Poid itemPoid = payFlist.get(FldPoid.getInst());
					FList itemFlist = connector.arGetItemDetails(itemPoid);
					
					if(payFlist.containsKey(FldPayType.getInst())) {
						payInfo.setPaymentMode(getPaymentType(payFlist.get(FldPayType.getInst())));
						String refNumber = getPaymentReference(itemFlist, payFlist.get(FldPayType.getInst()));
						payInfo.setRefNumber(refNumber);
						payInfo.setStatus("SUCCESS");
					} else {
						payInfo.setPaymentMode("UNKNOWN");
					}
					
					log.info("the itemFlist flist: " +itemFlist);
					
					paymentList.add(payInfo);
				}
			}
		}catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			
		} finally {
			log.info("Connector completes processing for getPaymentDetails");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		
	    	}
		}		
		return paymentList;
	}
	
	public PaymentList getPaymentDetails1(String accountNo, int count) {
		PaymentList paymentList = new PaymentList();
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			Poid accountPoid = connector.getAccountPoidFromAccountNo(accountNo);
			String poidType = "/item/payment,/item/payment/reversal,/item/refund";
			FList paymentFlist = connector.getAccountPayments(accountPoid, count, 6, poidType);
			log.info("the payment out flist: " +paymentFlist);
			
			if(paymentFlist.containsKey(FldResults.getInst())) {
				SparseArray payArray = paymentFlist.get(FldResults.getInst());
				Enumeration <?> payment = payArray.getValueEnumerator();
				while(payment.hasMoreElements()) {
					FList payFlist = (FList)payment.nextElement();
					PaymentInfo payInfo = new PaymentInfo();
					payInfo.setAmount(payFlist.get(FldItemTotal.getInst()).doubleValue());
					payInfo.setName(payFlist.get(FldName.getInst()));
					payInfo.setPaymentDate(payFlist.get(FldCreatedT.getInst()));
					payInfo.setPaymentMode(getPaymentType(payFlist.get(FldPayType.getInst())));
					paymentList.addPayments(payInfo);
				}
			}
		}catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			paymentList.setSuccess(false);
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			paymentList.setSuccess(false);
			
		} finally {
			log.info("Connector completes processing for getPaymentDetails");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		paymentList.setSuccess(false);
	    	}
		}
				
		return paymentList;
	}
	
	private String getPaymentType(int payType) {
		
		for(BrmConstants.PayType pay : BrmConstants.PayType.values()) {
			if(pay.value() == payType)
				return pay.toString();
		}
		
		return "Invalid";
	}
	
	private String getPaymentReference(FList inputFlist, int payType) throws EBufException {
		String refNumber = "";
		if(! inputFlist.containsKey(FldResults.getInst())) 
			return refNumber;
		FList resultFlist = inputFlist.getElement(FldResults.getInst(), 0);
		
		if(! resultFlist.containsKey(FldEvents.getInst())) 
			return refNumber;
		
		FList eventFlist = resultFlist.getElement(FldEvents.getInst(), 0);
		
		switch(payType)
		{
		case PortalEnums.PinPayType.CASH:
			if(eventFlist.containsKey(FldCashInfo.getInst())) {
				FList infoFlist = eventFlist.getElement(FldCashInfo.getInst(), 0);
				refNumber = infoFlist.get(FldReceiptNo.getInst());
			}
			break;
		case PortalEnums.PinPayType.CHECK:
			if(eventFlist.containsKey(FldCheckInfo.getInst())) {
				FList infoFlist = eventFlist.getElement(FldCheckInfo.getInst(), 0);
				refNumber = infoFlist.get(FldCheckNo.getInst());
			}
			break;
		
		default:
			break;					
		}		
			
		return refNumber;
	}
	
	public Response postPayment(Payment payment) {
		Response response = new Response();
		
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			Poid accountPoid = connector.getAccountPoidFromAccountNo(payment.getAccountNo());
			FList paymentFlist = new FList();
			paymentFlist.set(FldPoid.getInst(), accountPoid);
			paymentFlist.set(FldProgramName.getInst(), "SFDC - Manual Payment");
			FList chargesFlist = prepareChargesFlist(accountPoid, payment);
			
			paymentFlist.setElement(FldCharges.getInst(), 0, chargesFlist);
			
			log.info("the pymt flist is" +paymentFlist);
			
			FList outFlist = connector.callOpcode(PortalOp.PYMT_COLLECT, paymentFlist, "postPayment");
			FList resultFlist = outFlist.getElement(FldResults.getInst(), 0);
			int result = resultFlist.get(FldResult.getInst());
			if (result == PortalConst.BOOLEAN_TRUE) {
				response.setResponseCode(0);
				response.setResponseMsg("SUCCESS");
			}
		} catch (BRMException ex) {
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
			log.info("Connector completes processing for postPayment");
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
	
	private FList prepareChargesFlist(Poid accountPoid, Payment payment) {
		FList chargesFlist = new FList();
		chargesFlist.set(FldAccountObj.getInst(), accountPoid);
		chargesFlist.set(FldAmount.getInst(), new BigDecimal(payment.getAmount()));
		chargesFlist.set(FldCommand.getInst(), PortalEnums.PinChargeCmd.NONE);
		
		FList infoFlist = new FList();
		FList inhFlist = new FList();
		FList payFlist = new FList();
		switch(payment.getPaymentMode())
		{
		case "CASH":
			chargesFlist.set(FldPayType.getInst(), BrmConstants.PayType.CASH.value());
			infoFlist.set(FldReceiptNo.getInst(), payment.getRefNumber());
			inhFlist.setElement(FldCashInfo.getInst(), 0, infoFlist);
			break;
		case "CHEQUE":
			chargesFlist.set(FldPayType.getInst(), BrmConstants.PayType.CHEQUE.value());
			infoFlist.set(FldCheckNo.getInst(), payment.getRefNumber());
			infoFlist.set(FldBankCode.getInst(), payment.getBankCode());
			infoFlist.set(FldBankAccountNo.getInst(), payment.getBankAccountNo());
			inhFlist.setElement(FldCheckInfo.getInst(), 0, infoFlist);
			break;
		default:
			break;					
		}		
		payFlist.set(FldInheritedInfo.getInst(), inhFlist);
		chargesFlist.set(FldPayment.getInst(), payFlist);
		return chargesFlist;
		
	}
	
}
