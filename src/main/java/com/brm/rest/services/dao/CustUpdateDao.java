package com.brm.rest.services.dao;

import java.util.Enumeration;
import java.util.List;
import org.apache.log4j.Logger;
import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.update.CustUpdate;
import com.brm.service.portal.bean.update.NameInfo;
import com.brm.service.portal.bean.update.PayInfo;
import com.brm.service.portal.utils.BRMException;
import com.brm.service.portal.utils.ErrorCodes;
import com.portal.pcm.*;
import com.portal.pcm.fields.*;

public class CustUpdateDao {
	private static final Logger log = Logger.getLogger("Connector");

	public Response SuspendAccount(String acctNo) {
		
		PCMWrapper connector = new PCMWrapper();
		Response response = new Response();
		try {
			connector.createContext();
			FList outFlist = connector.getAccountByAccountNo(acctNo);
			FList suspendFlist = new FList();
			suspendFlist.set(FldPoid.getInst(), outFlist.get(FldPoid.getInst()));
			suspendFlist.set(FldProgramName.getInst(), "Rest Call for Suspend");
			suspendFlist.set(FldDescr.getInst(), "Suspending the account");
			FList statusesFlist = new FList();
			statusesFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_MANUAL);
			statusesFlist.set(FldStatus.getInst(), PortalEnums.PinStatus.INACTIVE);
			suspendFlist.setElement(FldStatuses.getInst(), 0, statusesFlist);
			
			log.info("the suspend flist is : " +suspendFlist);
			FList retFlist = connector.callOpcode(PortalOp.CUST_SET_STATUS, suspendFlist, "SuspendAccount");
			
			response.setResponseMsg("Success");
			response.setResponseCode(ErrorCodes.SUCCESS);
		}catch (BRMException ex) {
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
			log.info("Connector completes processing for Suspend Account");
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
	
	public Response UnSuspendAccount(String acctNo) {
		
		Response response = new Response();
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			FList outFlist = connector.getAccountByAccountNo(acctNo);
			FList unSusFlist = new FList();
			unSusFlist.set(FldPoid.getInst(), outFlist.get(FldPoid.getInst()));
			unSusFlist.set(FldProgramName.getInst(), "Rest Call for UnSuspend");
			unSusFlist.set(FldDescr.getInst(), "Unsuspending the account");
			FList statusesFlist = new FList();
			statusesFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_MANUAL);
			statusesFlist.set(FldStatus.getInst(), PortalEnums.PinStatus.ACTIVE);
			unSusFlist.setElement(FldStatuses.getInst(), 0, statusesFlist);
			
			log.info("the unsuspend flist is : " +unSusFlist);
			
			FList retFlist = connector.callOpcode(PortalOp.CUST_SET_STATUS, unSusFlist, "UnSuspendAccount");
			response.setResponseMsg("Success");
			response.setResponseCode(ErrorCodes.SUCCESS);
		}catch (BRMException ex) {
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
			log.info("Connector completes processing for UnSuspsend Account");
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
	
public Response CloseAccount(String acctNo) {
		
		Response response = new Response();
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			FList outFlist = connector.getAccountByAccountNo(acctNo);
			FList closeFlist = new FList();
			closeFlist.set(FldPoid.getInst(), outFlist.get(FldPoid.getInst()));
			closeFlist.set(FldProgramName.getInst(), "Rest Call for Terminate");
			closeFlist.set(FldDescr.getInst(), "Terminating the account");
			FList statusesFlist = new FList();
			statusesFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_MANUAL);
			statusesFlist.set(FldStatus.getInst(), PortalEnums.PinStatus.CLOSED);
			closeFlist.setElement(FldStatuses.getInst(), 0, statusesFlist);
			
			log.info("the unsuspend flist is : " +closeFlist);
			
			FList retFlist = connector.callOpcode(PortalOp.CUST_SET_STATUS, closeFlist, "CloseAccount");
			response.setResponseMsg("Success");
			response.setResponseCode(ErrorCodes.SUCCESS);
		}catch (BRMException ex) {
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
			log.info("Connector completes processing for UnSuspsend Account");
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
	
	
	public Response UpdateCustomer(String accountNo, CustUpdate custInfo) {
		
		Response response = new Response();
		PCMWrapper connector = new PCMWrapper();
		log.info("got update request for account:" +accountNo);
		Poid accountPoid = null;
		try {
			connector.createContext();
			FList outFlist = connector.getAccountByAccountNo(accountNo);
			String prgName = "Update Customer";
			accountPoid = outFlist.get(FldPoid.getInst());
			FList inputFlist = new FList();
			FList appendFlist = null;
			inputFlist.set(FldPoid.getInst(), accountPoid);
			inputFlist.set(FldAccountObj.getInst(), accountPoid);

			/*
			 * PAYINFO updates
			 */
			if(custInfo.getPayInfo() != null ) {
				if(custInfo.getPayInfo().getAction().equalsIgnoreCase("INITIAL") || 
						custInfo.getPayInfo().getAction().equalsIgnoreCase("ADD")) {
					
					log.info("Adding Payinfo");
					prgName = prgName + " - Payinfo (INITIAL/ADD)";
					appendFlist = addPayinfo(accountPoid, connector, custInfo.getPayInfo());
					
				} else if(custInfo.getPayInfo().getAction().equalsIgnoreCase("UPDATE")) {
					
					log.info("Updating Payinfo");
					prgName = prgName + " - Payinfo (UPDATE)";

					FList payFlist = connector.getCustomerPayinfo(inputFlist.get(FldPoid.getInst()), null, 0);
					if(! payFlist.containsKey(FldResults.getInst())){
						throw new BRMException("No Payinfo found for Update", ErrorCodes.POID_NOT_FOUND);
					}
					FList resultFlist = payFlist.getElement(FldResults.getInst(), 0);
					appendFlist = updatePayinfo(custInfo.getPayInfo(), inputFlist, resultFlist.get(FldPoid.getInst()));
					inputFlist.append(appendFlist);
					appendFlist = null;
					
				} else if(custInfo.getPayInfo().getAction().equalsIgnoreCase("DELETE")) {
					
					log.info("Deleting Payinfo");

					String payType;
					if(custInfo.getPayInfo().getCcInfo() != null)
						payType = "/payinfo/cc";
					else if(custInfo.getPayInfo().getDdInfo() != null)
						payType = "/payinfo/dd";
					else
						payType = "/payinfo/invoice";
					
					FList payInfoFlist = connector.getCustomerPayinfo(accountPoid, payType, 0);
					
					if(! payInfoFlist.containsKey(FldResults.getInst())) {
						throw new BRMException("No Payinfo found for deleting for the given paytype", ErrorCodes.ERR_TYPE_MISMATCH);
					}
					
					SparseArray resultArray = payInfoFlist.get(FldResults.getInst());
					Enumeration<?> results = resultArray.getKeyEnumerator();
					int key;
					while(results.hasMoreElements()) {
						key = (Integer)results.nextElement();
// key = 0 is the active payinfo so dont delete it.						
						if( key != 0) {
							FList deleteFlist = (FList)resultArray.elementAt(key);
							log.info("the delete flist is" +deleteFlist);
							FList delOutFlist = connector.callOpcode(PortalOp.CUST_DELETE_PAYINFO, 
									deleteFlist, "UpdateCustomer - Delete Payinfo");
							if(!delOutFlist.containsKey(FldPoid.getInst()))
								throw new BRMException("Error while deleting Payinfo", ErrorCodes.ERR_UNKNOWN_ERROR);
						}
					}
					
					response.setResponseMsg("Success");
					response.setResponseCode(ErrorCodes.SUCCESS);
					return response;
				}
			}
	
			/*
			 * BILLINFO updates
			 */
			if(custInfo.getBillInfo() != null) {
				FList bInfoFlist = null;
				if(appendFlist != null) {
					bInfoFlist = appendFlist.getElement(FldBillinfo.getInst(), 0);
				} else {
					FList billInfoFlist = connector.getBillinfoFromAccount(accountPoid);
					bInfoFlist = new FList();
					bInfoFlist.set(FldPoid.getInst(), billInfoFlist.get(FldPoid.getInst()));
					bInfoFlist.set(FldPayType.getInst(), billInfoFlist.get(FldPayType.getInst()));
					appendFlist = new FList();
					appendFlist.setElement(FldBillinfo.getInst(), 0, bInfoFlist);
					
				}
				if(custInfo.getBillInfo().getBillWhen() != -1) {
					bInfoFlist.set(FldBillWhen.getInst(), custInfo.getBillInfo().getBillWhen());
				}
				if(custInfo.getBillInfo().getBillCycleDay() != -1) {
					bInfoFlist.set(FldActgFutureDom.getInst(), custInfo.getBillInfo().getBillCycleDay());
				}
			}
			
			if(appendFlist != null)
				inputFlist.append(appendFlist);
			
			/*
			 * NAMEINFO updates
			 */
			appendFlist = null;
			if(! custInfo.getNameInfoList().isEmpty()) {
				FList readFList = new FList();
				readFList.set(FldPoid.getInst(), accountPoid);
				readFList.setElement(FldNameinfo.getInst(), Element.ELEMID_ANY);
				FList rOutFlist = connector.callOpcode(PortalOp.READ_FLDS, readFList, 
									"UpdateCustomer -Nameinfo ReadFlds");
				
				appendFlist = processNameinfo(custInfo.getNameInfoList(), rOutFlist);
			}
			
			if(appendFlist != null)
				inputFlist.append(appendFlist);
			
			inputFlist.set(FldProgramName.getInst(), prgName);
			
			log.info("the update customer input flist:" +inputFlist);
			
			connector.openTransaction(accountPoid);
			
			FList ouFlist = connector.callOpcode(PortalOp.CUST_UPDATE_CUSTOMER, inputFlist, "UpdateCustomer - Payinfo");
			log.info("the customer update output flist: " +ouFlist);
			
			if(custInfo.getPayInfo() != null &&
			   custInfo.getPayInfo().getAction().equalsIgnoreCase("INITIAL")) {
				SubscriptionDao subs = new SubscriptionDao();
				subs.setProductsActive(connector, outFlist.get(FldPoid.getInst()));
			}
			
			connector.commitTransaction(accountPoid);
			response.setResponseMsg("Success");
			response.setResponseCode(ErrorCodes.SUCCESS);
			
		}catch (BRMException ex) {
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
			log.info("Connector completes processing for Update Customer");
			try {
				if (connector != null && connector.isTransactionOpen()) {
					connector.abortTransaction(accountPoid);
					log.info("Transaction aborted successfully");
				}
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		
	    	}
		
		}
		
		return response;
	}
	
	private FList addPayinfo(Poid accountPoid, PCMWrapper connector, PayInfo payInfo) throws BRMException , EBufException {
		
		FList returnFlist = new FList();
		FList billInfoFlist = connector.getBillinfoFromAccount(accountPoid);
		FList bInfoFlist = new FList();
		bInfoFlist.set(FldPoid.getInst(), billInfoFlist.get(FldPoid.getInst()));
		bInfoFlist.setElement(FldPayinfo.getInst(), 1);
		returnFlist.setElement(FldBillinfo.getInst(), 0, bInfoFlist);
		
		FList payInfoFList = prepareInheritedInfo(bInfoFlist, payInfo);
		returnFlist.setElement(FldPayinfo.getInst(), 1, payInfoFList);
		
		return returnFlist;
		
	}
	private FList prepareInheritedInfo(FList bInfoFlist, PayInfo payInfo)  {

		FList typeFlist = new FList();
		FList inhInfoFlist = new FList();
		FList payInfoFlist = new FList();
		
		payInfoFlist.set(FldPoid.getInst(), new Poid(1, -1, "/payinfo"));
		
		if(payInfo.getCcInfo() != null) {
			
			
			typeFlist.set(FldSecurityId.getInst(), payInfo.getCcInfo().getSecurityId());
			typeFlist.set(FldName.getInst(), payInfo.getCcInfo().getName());
			typeFlist.set(FldDebitExp.getInst(), payInfo.getCcInfo().getDebitExp());
			if(payInfo.getCcInfo().getDebitToken() != null && 
					! payInfo.getCcInfo().getDebitToken().equalsIgnoreCase(""))
				typeFlist.set(FldDebitNum.getInst(), payInfo.getCcInfo().getDebitToken());
			else
				typeFlist.set(FldDebitNum.getInst(), payInfo.getCcInfo().getDebitNum());

			typeFlist.set(FldCountry.getInst(), payInfo.getCcInfo().getCountry());
			typeFlist.set(FldZip.getInst(), payInfo.getCcInfo().getZip());
			typeFlist.set(FldState.getInst(), payInfo.getCcInfo().getState());
			typeFlist.set(FldCity.getInst(), payInfo.getCcInfo().getCity());
			typeFlist.set(FldAddress.getInst(), payInfo.getCcInfo().getAddress());
			
			inhInfoFlist.setElement(FldCcInfo.getInst(), 0, typeFlist);
			payInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.CC);
			bInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.CC);


		} else if(payInfo.getDdInfo() != null) {
			log.info("Adding Payinfo- Debit");
			
			typeFlist.set(FldAddress.getInst(), payInfo.getDdInfo().getAddress());
			typeFlist.set(FldBankNo.getInst(), payInfo.getDdInfo().getBankNo());
			typeFlist.set(FldCity.getInst(), payInfo.getDdInfo().getCity());
			typeFlist.set(FldCountry.getInst(), payInfo.getDdInfo().getCountry());
			typeFlist.set(FldDebitNum.getInst(), payInfo.getDdInfo().getDebitNum());
			typeFlist.set(FldName.getInst(), payInfo.getDdInfo().getName());
			typeFlist.set(FldState.getInst(), payInfo.getDdInfo().getState());
			typeFlist.set(FldZip.getInst(), payInfo.getDdInfo().getZip());
			switch(payInfo.getDdInfo().getType())
			{
			case "savings":
				typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_SAVINGS);
				break;
				
			case "checking":
				typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_CHECKING);
				break;
				
			case "corporate":
				typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_CORPORATE);
				break;
				
			default:
				typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_SAVINGS);
			
			}
			inhInfoFlist.setElement(FldDdInfo.getInst(), 0, typeFlist);
			payInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.DD);
			bInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.DD);
			
		} else if(payInfo.getInvInfo() != null) {
			log.info("Adding Payinfo- Invoice");
			typeFlist.set(FldAddress.getInst(), payInfo.getInvInfo().getAddress());
			typeFlist.set(FldCity.getInst(), payInfo.getInvInfo().getCity());
			typeFlist.set(FldCountry.getInst(), payInfo.getInvInfo().getCountry());
			
			typeFlist.set(FldDeliveryDescr.getInst(), payInfo.getInvInfo().getDeliveryDescr());
			switch(payInfo.getInvInfo().getDeliveryPrefer())
			{
			case "email":
				typeFlist.set(FldDeliveryPrefer.getInst(), PortalEnums.PinDeliveryPrefer.INV_EMAIL_DELIVERY);
				break;
				
			case "postal":
				typeFlist.set(FldDeliveryPrefer.getInst(), PortalEnums.PinDeliveryPrefer.INV_USP_DELIVERY);
				break;
				
			case "fax":
				typeFlist.set(FldDeliveryPrefer.getInst(), PortalEnums.PinDeliveryPrefer.INV_FAX_DELIVERY);
				break;
				
			default:
				typeFlist.set(FldType.getInst(), PortalEnums.PinDeliveryPrefer.INV_USP_DELIVERY);
			
			}
			
			typeFlist.set(FldInvInstr.getInst(), payInfo.getInvInfo().getInvInstr());
			typeFlist.set(FldInvTerms.getInst(), PortalEnums.PinInvTerms.UNDEFINED);
			typeFlist.set(FldEmailAddr.getInst(), payInfo.getInvInfo().getEmailAddr());
			typeFlist.set(FldName.getInst(), payInfo.getInvInfo().getName());
			typeFlist.set(FldState.getInst(), payInfo.getInvInfo().getState());
			typeFlist.set(FldZip.getInst(), payInfo.getInvInfo().getZip());
			
			inhInfoFlist.setElement(FldInvInfo.getInst(), 0, typeFlist);
			payInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.INVOICE);
			bInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.INVOICE);
		}
		payInfoFlist.set(FldFlags.getInst(), 1);
		payInfoFlist.set(FldInheritedInfo.getInst(), inhInfoFlist);
		
		return payInfoFlist;
	}
	

	private FList updatePayinfo(PayInfo payInfo, 
				FList inputFlist, Poid payinfoPoid) throws BRMException, EBufException {
		
		FList typeFlist = new FList();
		FList payInfoFlist = new FList();
		FList inhInfoFlist = new FList();
		FList returnFlist = new FList();
		
		if(payInfo.getCcInfo() != null) {
			log.info("updating cc info" +inputFlist);
			if(! payinfoPoid.getType().equalsIgnoreCase("/payinfo/cc"))
				throw new BRMException("Customer's payment method is not Credit Card", ErrorCodes.ERR_TYPE_MISMATCH);
			
			setNonEmptyField(typeFlist, FldAddress.getInst(), payInfo.getCcInfo().getAddress());
			setNonEmptyField(typeFlist, FldCity.getInst(), payInfo.getCcInfo().getCity());
			setNonEmptyField(typeFlist, FldCountry.getInst(), payInfo.getCcInfo().getCountry());
			setNonEmptyField(typeFlist, FldDebitExp.getInst(), payInfo.getCcInfo().getDebitExp());
			setNonEmptyField(typeFlist, FldDebitNum.getInst(), payInfo.getCcInfo().getDebitNum());
			setNonEmptyField(typeFlist, FldDebitNum.getInst(), payInfo.getCcInfo().getDebitToken());
			setNonEmptyField(typeFlist, FldName.getInst(), payInfo.getCcInfo().getName());
			setNonEmptyField(typeFlist, FldSecurityId.getInst(), payInfo.getCcInfo().getSecurityId());
			setNonEmptyField(typeFlist, FldState.getInst(), payInfo.getCcInfo().getState());
			setNonEmptyField(typeFlist, FldZip.getInst(), payInfo.getCcInfo().getZip());

			inhInfoFlist.setElement(FldCcInfo.getInst(), 0, typeFlist);
			payInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.CC);
			
		}  else if(payInfo.getDdInfo() != null) {
			log.info("updating dd info");
			if(! payinfoPoid.getType().equalsIgnoreCase("/payinfo/dd"))
				throw new BRMException("Customer's payment method is not Debit Card", ErrorCodes.ERR_TYPE_MISMATCH);
			
			setNonEmptyField(typeFlist, FldAddress.getInst(), payInfo.getDdInfo().getAddress());
			setNonEmptyField(typeFlist, FldBankNo.getInst(), payInfo.getDdInfo().getBankNo());
			setNonEmptyField(typeFlist, FldCity.getInst(), payInfo.getDdInfo().getCity());
			setNonEmptyField(typeFlist, FldCountry.getInst(), payInfo.getDdInfo().getCountry());
			setNonEmptyField(typeFlist, FldDebitNum.getInst(), payInfo.getDdInfo().getDebitNum());
			setNonEmptyField(typeFlist, FldName.getInst(), payInfo.getDdInfo().getName());
			setNonEmptyField(typeFlist, FldState.getInst(), payInfo.getDdInfo().getState());
			setNonEmptyField(typeFlist, FldZip.getInst(), payInfo.getDdInfo().getZip());
			
			if(payInfo.getDdInfo().getType() != null) {
				switch(payInfo.getDdInfo().getType())
				{
				case "savings":
					typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_SAVINGS);
					break;
					
				case "checking":
					typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_CHECKING);
					break;
					
				case "corporate":
					typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_CORPORATE);
					break;
					
				default:
					typeFlist.set(FldType.getInst(), PortalEnums.PinAcctTypes.TYPE_SAVINGS);

				}
			}

			inhInfoFlist.setElement(FldDdInfo.getInst(), 0, typeFlist);
			payInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.DD);
			
		}  else if(payInfo.getInvInfo() != null) {
			log.info("updating invoice info");
			if(! payinfoPoid.getType().equalsIgnoreCase("/payinfo/invoice"))
				throw new BRMException("Customer's payment method is not Invoice", ErrorCodes.ERR_TYPE_MISMATCH);
			setNonEmptyField(typeFlist, FldAddress.getInst(), payInfo.getInvInfo().getAddress());
			setNonEmptyField(typeFlist, FldCity.getInst(), payInfo.getInvInfo().getCity());
			setNonEmptyField(typeFlist, FldCountry.getInst(), payInfo.getInvInfo().getCountry());
			setNonEmptyField(typeFlist, FldDeliveryDescr.getInst(), payInfo.getInvInfo().getDeliveryDescr());
			setNonEmptyField(typeFlist, FldInvInstr.getInst(), payInfo.getInvInfo().getInvInstr());
			setNonEmptyField(typeFlist, FldEmailAddr.getInst(), payInfo.getInvInfo().getEmailAddr());
			setNonEmptyField(typeFlist, FldName.getInst(), payInfo.getInvInfo().getName());
			setNonEmptyField(typeFlist, FldState.getInst(), payInfo.getInvInfo().getState());
			setNonEmptyField(typeFlist, FldZip.getInst(), payInfo.getInvInfo().getZip());
			typeFlist.set(FldInvTerms.getInst(), PortalEnums.PinInvTerms.UNDEFINED);
			
			if(payInfo.getInvInfo().getDeliveryPrefer() != null) {
				switch(payInfo.getInvInfo().getDeliveryPrefer())
				{
				case "email":
					typeFlist.set(FldDeliveryPrefer.getInst(), PortalEnums.PinDeliveryPrefer.INV_EMAIL_DELIVERY);
					break;
					
				case "postal":
					typeFlist.set(FldDeliveryPrefer.getInst(), PortalEnums.PinDeliveryPrefer.INV_USP_DELIVERY);
					break;
					
				case "fax":
					typeFlist.set(FldDeliveryPrefer.getInst(), PortalEnums.PinDeliveryPrefer.INV_FAX_DELIVERY);
					break;
					
				default:
					typeFlist.set(FldType.getInst(), PortalEnums.PinDeliveryPrefer.INV_USP_DELIVERY);
				
				}
			}

			inhInfoFlist.setElement(FldInvInfo.getInst(), 0, typeFlist);
			payInfoFlist.set(FldPayType.getInst(), PortalEnums.PinPayType.INVOICE);
		}
		
		payInfoFlist.set(FldPoid.getInst(), payinfoPoid);
		payInfoFlist.set(FldInheritedInfo.getInst(), inhInfoFlist);
		if(payInfo.getPaymentTerm() != -1)
			payInfoFlist.set(FldPaymentTerm.getInst(), payInfo.getPaymentTerm());
		if(payInfo.getInvType() != 0 )
			payInfoFlist.set(FldInvType.getInst(), payInfo.getInvType());
		
		returnFlist.setElement(FldPayinfo.getInst(), 0, payInfoFlist);
		return returnFlist;
	}
	
	private void setNonEmptyField(FList inputFlist, StrField field, String value) {
		if(value != null) {
			inputFlist.set(field, value);
		}
	}
	
	private FList processNameinfo(List<NameInfo> nameInfoList, FList inFlist) throws BRMException, EBufException {
		FList returnFlist =  new FList();;
		String prgName = "Update Customer - NameInfo";
		for(NameInfo nameInfo : nameInfoList) {
			
			int recId = nameInfo.getId();
			
			if(nameInfo.getAction().equalsIgnoreCase("DELETE")) {
				if(inFlist.getElement(FldNameinfo.getInst(), recId) == null)
					throw new BRMException("NameInfo with recId '" +recId+ "' not found for Deletion.", ErrorCodes.ERR_TYPE_MISMATCH);
				
				log.info("Deleteing NameInfo:" +recId);
				prgName = prgName + " (DELETE)";
				returnFlist.setElement(FldNameinfo.getInst(), recId);				
			} else if(nameInfo.getAction().equalsIgnoreCase("UPDATE") ||
					nameInfo.getAction().equalsIgnoreCase("ADD")) {
				
				if(nameInfo.getAction().equalsIgnoreCase("UPDATE")) {
						if(inFlist.getElement(FldNameinfo.getInst(), recId) == null)
						throw new BRMException("NameInfo with recId '" +recId+ "' not found for Updation.", ErrorCodes.ERR_TYPE_MISMATCH);
						log.info("Updating NameInfo:" +recId);
						prgName = prgName + " (UPDATE)";
				}
				
				if(nameInfo.getAction().equalsIgnoreCase("ADD")) {
						if(inFlist.getElement(FldNameinfo.getInst(), recId) != null)
						throw new BRMException("NameInfo with recId '" +recId+ "' already present. Kindly Update.", ErrorCodes.ERR_TYPE_MISMATCH);
						log.info("Adding NameInfo:" +recId);
						prgName = prgName + " (ADD)";
				}
						

				FList nameInfoFlist = new FList();
				setNonEmptyField(nameInfoFlist, FldFirstName.getInst(), nameInfo.getFirstName());
				setNonEmptyField(nameInfoFlist, FldLastName.getInst(), nameInfo.getLastName());		
				setNonEmptyField(nameInfoFlist, FldSalutation.getInst(), nameInfo.getSalutation());
				setNonEmptyField(nameInfoFlist, FldEmailAddr.getInst(), nameInfo.getEmailAddress());
				setNonEmptyField(nameInfoFlist, FldCountry.getInst(), nameInfo.getCountry());
				setNonEmptyField(nameInfoFlist, FldZip.getInst(), nameInfo.getZip());
				setNonEmptyField(nameInfoFlist, FldState.getInst(), nameInfo.getState());
				setNonEmptyField(nameInfoFlist, FldCity.getInst(), nameInfo.getCity());
				setNonEmptyField(nameInfoFlist, FldAddress.getInst(), nameInfo.getAddress());
				setNonEmptyField(nameInfoFlist, FldMiddleName.getInst(), nameInfo.getMiddleName());
				setNonEmptyField(nameInfoFlist, FldTitle.getInst(), nameInfo.getTitle());
				setNonEmptyField(nameInfoFlist, FldCompany.getInst(), nameInfo.getCompany());
				setNonEmptyField(nameInfoFlist, FldContactType.getInst(), nameInfo.getContactType());
				returnFlist.setElement(FldNameinfo.getInst(), recId, nameInfoFlist);
			}
		}
		
		returnFlist.set(FldProgramName.getInst(), prgName);
		return returnFlist;
		
	}
	
}
