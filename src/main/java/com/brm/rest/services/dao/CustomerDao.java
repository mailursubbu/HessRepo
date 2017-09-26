package com.brm.rest.services.dao;


import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.brm.service.portal.FlistCreator;
import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.customer.AccountInfo;
import com.brm.service.portal.bean.customer.BillInfo;
import com.brm.service.portal.bean.customer.CustomerInfo;
import com.brm.service.portal.bean.customer.CustomerNameInfo;
import com.brm.service.portal.bean.customer.CustomerPayInfo;
import com.brm.service.portal.bean.customer.PhoneInfo;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.PortalConst;
import com.portal.pcm.PortalContext;
import com.portal.pcm.PortalEnums;
import com.portal.pcm.PortalOp;
import com.portal.pcm.SparseArray;
import com.portal.pcm.fields.FldAccountObj;
import com.portal.pcm.fields.FldActgCycleDom;
import com.portal.pcm.fields.FldAddress;
import com.portal.pcm.fields.FldBankNo;
import com.portal.pcm.fields.FldBillWhen;
import com.portal.pcm.fields.FldCcInfo;
import com.portal.pcm.fields.FldCity;
import com.portal.pcm.fields.FldCode;
import com.portal.pcm.fields.FldCountry;
import com.portal.pcm.fields.FldCreatedT;
import com.portal.pcm.fields.FldCurrency;
import com.portal.pcm.fields.FldDdInfo;
import com.portal.pcm.fields.FldDealInfo;
import com.portal.pcm.fields.FldDealObj;
import com.portal.pcm.fields.FldDeals;
import com.portal.pcm.fields.FldDebitExp;
import com.portal.pcm.fields.FldDebitNum;
import com.portal.pcm.fields.FldDiscounts;
import com.portal.pcm.fields.FldEffectiveT;
import com.portal.pcm.fields.FldEmailAddr;
import com.portal.pcm.fields.FldFirstName;
import com.portal.pcm.fields.FldInvInfo;
import com.portal.pcm.fields.FldLastBillT;
import com.portal.pcm.fields.FldLastName;
import com.portal.pcm.fields.FldModT;
import com.portal.pcm.fields.FldName;
import com.portal.pcm.fields.FldNameinfo;
import com.portal.pcm.fields.FldNextBillT;
import com.portal.pcm.fields.FldPermitted;
import com.portal.pcm.fields.FldPhone;
import com.portal.pcm.fields.FldPhones;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldProducts;
import com.portal.pcm.fields.FldReadAccess;
import com.portal.pcm.fields.FldResults;
import com.portal.pcm.fields.FldSalutation;
import com.portal.pcm.fields.FldServices;
import com.portal.pcm.fields.FldState;
import com.portal.pcm.fields.FldStatus;
import com.portal.pcm.fields.FldStatusFlags;
import com.portal.pcm.fields.FldType;
import com.portal.pcm.fields.FldWriteAccess;
import com.portal.pcm.fields.FldZip;

public class CustomerDao {
	
	private CustomerInfo custInfo;
	public CustomerDao() {
		super();
		custInfo = new CustomerInfo();
		custInfo.setErrorCode(101);
		custInfo.setErrorMsg("Generic Error");
		
		// TODO Auto-generated constructor stub
	}

	private static final Logger log = Logger.getLogger(CustomerDao.class);
		
	public CustomerInfo getCustomerDetails(String acctNo){
		PCMWrapper connector = new PCMWrapper();
		CustomerNameInfo custNameInfo = null;
		BillInfo bInfo = new BillInfo();
		CustomerPayInfo payInfo = null;
		
		try {
			connector.createContext();
			FList outFlist = connector.getAccountByAccountNo(acctNo);
									
			custInfo.setAccountNo(acctNo);
			custInfo.setAccountPoidString(outFlist.get(FldPoid.getInst()).toString());
			custInfo.setCurrency(outFlist.get(FldCurrency.getInst()).toString());
			custInfo.setEffectiveT(outFlist.get(FldEffectiveT.getInst()));
			
			switch(outFlist.get(FldStatus.getInst())) {
			case PortalEnums.PinStatus.ACTIVE :
				custInfo.setStatus("ACTIVE");
				break;
			case PortalEnums.PinStatus.INACTIVE :
				custInfo.setStatus("SUSPENDED");
				break;
			case PortalEnums.PinStatus.CLOSED :
				custInfo.setStatus("CLOSED");
				break;
			default :
				break;
			}
			
			if (outFlist.containsKey(FldNameinfo.getInst())) {
				int id = 0;
				SparseArray nameArray = outFlist.get(FldNameinfo.getInst());
				Enumeration <?> nameInfo = nameArray.getKeyEnumerator();
				while (nameInfo.hasMoreElements()) {
					id = (Integer)nameInfo.nextElement();
					FList nameInfoFlist = (FList)nameArray.elementAt(id);
					custNameInfo = new CustomerNameInfo();
					custNameInfo.setId(id);
					custNameInfo.setAddress(nameInfoFlist.get(FldAddress.getInst()));
					custNameInfo.setCity(nameInfoFlist.get(FldCity.getInst()));
					custNameInfo.setCountry(nameInfoFlist.get(FldCountry.getInst()));
					custNameInfo.setEmailAddress(nameInfoFlist.get(FldEmailAddr.getInst()));
					custNameInfo.setFirstName(nameInfoFlist.get(FldFirstName.getInst()));
					custNameInfo.setLastName(nameInfoFlist.get(FldLastName.getInst()));
					custNameInfo.setSalutation(nameInfoFlist.get(FldSalutation.getInst()));
					custNameInfo.setState(nameInfoFlist.get(FldState.getInst()));
					custNameInfo.setZip(nameInfoFlist.get(FldZip.getInst()));
					if(nameInfoFlist.containsKey(FldPhones.getInst())) {
						SparseArray phonesArray = nameInfoFlist.get(FldPhones.getInst());
						Enumeration<?> phoneInfo = phonesArray.getValueEnumerator();
						while(phoneInfo.hasMoreElements()) {
							FList phonesFlist = (FList)phoneInfo.nextElement();
							PhoneInfo phones = new PhoneInfo();
							phones.setPhoneNo(phonesFlist.get(FldPhone.getInst()));
							phones.setType(phonesFlist.get(FldType.getInst()));
							custNameInfo.addPhoneInfoList(phones);
						}
					}
					custInfo.addNameInfoList(custNameInfo);
				}
			}
			
			FList billInfoFlist = connector.getBillinfoFromAccount(outFlist.get(FldPoid.getInst()));
		//	log.info("the billinfo flist" +billInfoFlist);
			bInfo.setDom(billInfoFlist.get(FldActgCycleDom.getInst()));
			bInfo.setBillFrequency(billInfoFlist.get(FldBillWhen.getInst()));
			bInfo.setLastBillDate(billInfoFlist.get(FldLastBillT.getInst()));
			bInfo.setNextBillDate(billInfoFlist.get(FldNextBillT.getInst()));
			FList payInfoFlist = connector.getCustomerPayinfo(outFlist.get(FldPoid.getInst()), 
									null, PortalContext.OPFLG_READ_RESULT);
			payInfo = processPayinfo(payInfoFlist);
			
			custInfo.setErrorCode(0);
			custInfo.setErrorMsg("Success");
			
			
		}catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
			
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
			
		} finally {
			log.info("Connector completes processing for getCustomerDetails");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));
	    		
	    	}
		
		}
		custInfo.setBillInfo(bInfo);
		custInfo.setPayInfo(payInfo);
		return custInfo;

	}
	
	public CustomerPayInfo processPayinfo(FList inputFList) throws EBufException {
		
		CustomerPayInfo payInfo = new CustomerPayInfo();
		String mask = "xxxxxxxxxxxx####";
		if(! inputFList.containsKey(FldResults.getInst())){
			return payInfo;
		}
		
		FList resultFlist = inputFList.getElement(FldResults.getInst(), 0);
		FList invoiceFList = null;
		if(resultFlist.containsKey(FldCcInfo.getInst())) {
			
			payInfo.setPayType("CREDIT_CARD");
			invoiceFList = resultFlist.getElement(FldCcInfo.getInst(), 0);
			payInfo.setCardNo(maskNumber(invoiceFList.get(FldDebitNum.getInst()), mask));
			payInfo.setCardExp(invoiceFList.get(FldDebitExp.getInst()));
			
		} else if (resultFlist.containsKey(FldDdInfo.getInst())) {
			payInfo.setPayType("DEBIT_CARD");
			invoiceFList = resultFlist.getElement(FldDdInfo.getInst(), 0);
			payInfo.setCardNo(maskNumber(invoiceFList.get(FldDebitNum.getInst()), mask));
			payInfo.setBankNo(invoiceFList.get(FldBankNo.getInst()));
			
		} else {
			payInfo.setPayType("INVOICE");
			invoiceFList = resultFlist.getElement(FldInvInfo.getInst(), 0);
			payInfo.setEmailAddress(invoiceFList.get(FldEmailAddr.getInst()));
			
		} 
		
		payInfo.setAddress(invoiceFList.get(FldAddress.getInst()));
		payInfo.setCity(invoiceFList.get(FldCity.getInst()));
		payInfo.setCountry(invoiceFList.get(FldCountry.getInst()));
		payInfo.setName(invoiceFList.get(FldName.getInst()));
		payInfo.setState(invoiceFList.get(FldState.getInst()));
		payInfo.setZip(invoiceFList.get(FldZip.getInst()));
		
		return payInfo;
	}
	
	private String maskNumber(String number, String mask) {
		 
	      int index = 0;
	      StringBuilder masked = new StringBuilder();
	      for (int i = 0; i < mask.length(); i++) {
	         char c = mask.charAt(i);
	         if (c == '#') {
	            masked.append(number.charAt(index));
	            index++;
	         } else if (c == 'x') {
	            masked.append(c);
	            index++;
	         } else {
	            masked.append(c);
	         }
	      }
	      return masked.toString();
	   }
	
	public Response createCustomerAccount(AccountInfo accountInfo) {
		
		FlistCreator creator = new FlistCreator();
		Response response = new Response();
		response.setResponseMsg("Failure");
		response.setResponseCode(1);
		PCMWrapper connector = new PCMWrapper();
		log.info("Account creation request got");
		
		try {
			FList planInFlist = creator.getPoidFromStrField("/plan", 
					FldName.getInst(), accountInfo.getPlanName());
			
			connector.createContext();
			FList planOutFlist = connector.search(planInFlist, 256, "Search Plan by Name");
			log.info("the search plan outflist is: " +planOutFlist);
			
			FList planFlist = planOutFlist.getElement(FldResults.getInst(), 0);
			
			FList readPlanFlist = new FList();
			readPlanFlist.set(FldPoid.getInst(), planFlist.get(FldPoid.getInst()));
			FList outPlanFlist = connector.callOpcode(PortalOp.CUST_POL_READ_PLAN, 
						readPlanFlist, "createCustomerAccount-cust_pol_read_plan");
			log.info("the outplan flist is:" +outPlanFlist);
			if(!accountInfo.isActive()) {
				setProductInacvite(outPlanFlist);
			}
			
			FList inFlist = creator.prepareChildFlist(accountInfo, outPlanFlist);
			log.info("the child creation flist: " +inFlist);
			FList ouFlist = connector.callOpcode(PortalOp.CUST_COMMIT_CUSTOMER, inFlist, "createCustomerAccount");
			log.info("the child creation output: " +ouFlist);
			response.setResponseMsg("SUCCESS");
			response.setResponseCode(0);
			
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
			log.info("Connector completes processing for ");
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
	
	private void setProductInacvite(FList inputFlist) throws EBufException {
		if(inputFlist.containsKey(FldServices.getInst())) {
			SparseArray servicesArray = inputFlist.get(FldServices.getInst());			
			Enumeration <?> services = servicesArray.getValueEnumerator();
			while (services.hasMoreElements()) {
				FList serviceFlist = (FList)services.nextElement();
// if service has to be made inactive then				
				/*FList statusFlist = new FList();
				statusFlist.set(FldStatus.getInst(), PortalEnums.PinStatus.INACTIVE);
				statusFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_ACTIVATE);
				serviceFlist.setElement(FldStatuses.getInst(), 0, statusFlist);*/
				if(serviceFlist.containsKey(FldDeals.getInst())) {
					SparseArray dealsArray = serviceFlist.get(FldDeals.getInst());			
					Enumeration <?> deals = dealsArray.getValueEnumerator();
					while (deals.hasMoreElements()) {
						FList dealsFlist = (FList)deals.nextElement();
						FList dealInfoFlist = dealsFlist.deepClone();
						dealInfoFlist.remove(FldCode.getInst());
						dealInfoFlist.remove(FldAccountObj.getInst());
						dealInfoFlist.remove(FldPermitted.getInst());
						dealInfoFlist.remove(FldWriteAccess.getInst());
						dealInfoFlist.remove(FldReadAccess.getInst());
						dealInfoFlist.remove(FldModT.getInst());
						dealInfoFlist.remove(FldCreatedT.getInst());
						dealInfoFlist.set(FldPoid.getInst(), dealInfoFlist.get(FldDealObj.getInst()));
						dealInfoFlist.remove(FldDealObj.getInst());
						serviceFlist.set(FldDealInfo.getInst(), dealInfoFlist);
						serviceFlist.remove(FldDealObj.getInst());
						
						if(dealInfoFlist.containsKey(FldProducts.getInst())) {
							SparseArray productsArray = dealInfoFlist.get(FldProducts.getInst());			
							Enumeration <?> products = productsArray.getValueEnumerator();
							while (products.hasMoreElements()) {
								FList productsFlist = (FList)products.nextElement();
								productsFlist.set(FldStatus.getInst(), PortalEnums.PinProductStatus.INACTIVE);
								productsFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_ACTIVATE);
							}
						}
						if(dealInfoFlist.containsKey(FldDiscounts.getInst())) {
							SparseArray discountsArray = dealInfoFlist.get(FldDiscounts.getInst());			
							Enumeration <?> discounts = discountsArray.getValueEnumerator();
							while (discounts.hasMoreElements()) {
								FList discountsFlist = (FList)discounts.nextElement();
								discountsFlist.set(FldStatus.getInst(), PortalEnums.PinProductStatus.INACTIVE);
								discountsFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_ACTIVATE);
							}
						}
					}
						
				}
			}
		}
	}
}
