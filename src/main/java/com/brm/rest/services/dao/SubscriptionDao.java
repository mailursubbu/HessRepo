package com.brm.rest.services.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import com.brm.service.portal.FlistCreator;
import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.subscription.DealInfo;
import com.brm.service.portal.bean.subscription.ProdInfo;
import com.brm.service.portal.bean.subscription.PlanChangeInfo;
import com.brm.service.portal.bean.subscription.PurchaseOverride;
import com.brm.service.portal.utils.BRMException;
import com.brm.service.portal.utils.ErrorCodes;
import com.portal.pcm.*;
import com.portal.pcm.fields.*;

public class SubscriptionDao {
	
	private static final Logger log = Logger.getLogger("Connector");
	HashMap<String, Poid> serviceHash = new HashMap<String, Poid>();
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	public Response purchaseDealWorker(DealInfo dInfo) {
		
		log.info("Deal Purchase request got");
		log.info("for account no " +dInfo.getAccountNo());
		List<ProdInfo> dealList  = dInfo.getProdInfo();
		
		PCMWrapper connector = new PCMWrapper();
		Response response = new Response();
		Poid accountPoid = null;
		try {
			connector.createContext();
			accountPoid = connector.getAccountPoidFromAccountNo(dInfo.getAccountNo());			
			connector.openTransaction(accountPoid);
			for (ProdInfo pInfo : dealList) {
				log.info("*** purchaseDealWorker calling purchaseDeal ***");
				purchaseDeal(connector, accountPoid, pInfo);
				log.info("*** purchaseDealWorker calling purchaseDeal - SUCCESS ***");
			}
			
			/*for (Map.Entry m:serviceHash.entrySet()) {
				log.info("the key is:" +m.getKey());
				log.info("the value:" +m.getValue().toString());
			}*/
			//setProductsActive(connector, accountPoid);
			connector.commitTransaction(accountPoid);
			//connector.abortTransaction(accountPoid);
			response.setResponseMsg("SUCCESS");
			response.setResponseCode(ErrorCodes.SUCCESS);
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
			log.info("Connector completes processing for SubscriptionDao - purchaseDealWorker");
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
	
	private void purchaseDeal(PCMWrapper connector, Poid accountPoid, ProdInfo pInfo) throws BRMException, EBufException {
		FlistCreator creator = new FlistCreator();
		Poid servicePoid;
		Date givenDate = null; 
		Date startDate = null;
		Date endDate = null;
		String startDate_str = null;
		String endDate_str = null;
		FList dealInFlist = creator.getPoidFromStrField("/deal", 
				FldName.getInst(), pInfo.getName());
		FList dealOutFlist = connector.search(dealInFlist, 
							256, "Search Deal by Name");
		FList resultFlist = dealOutFlist.getElement(FldResults.getInst(), 0);
		log.info("Deal Details --> " + resultFlist);
		String permitted = resultFlist.get(FldPermitted.getInst());
		if (! permitted.equalsIgnoreCase("/account") && ! serviceHash.containsKey(permitted)) {
			log.info("getting the service poid for the type:" +permitted);
			servicePoid = connector.getServicePoidFromType(accountPoid, permitted);
			serviceHash.put(permitted, servicePoid);
		} else {
			log.info("service poid already present for the type:" +permitted);
			servicePoid = serviceHash.get(permitted);
		}
		log.info("the service poid is:" +servicePoid);
		FList purchaseFlist = new FList();
		purchaseFlist.set(FldPoid.getInst(), accountPoid);
		if(! permitted.equalsIgnoreCase("/account")) 
			purchaseFlist.set(FldServiceObj.getInst(), (Poid)serviceHash.get(permitted));
		
		purchaseFlist.set(FldProgramName.getInst(), "Subscription - PurchaseDeal");
		
		FList dealInfo = new FList();
		dealInfo.set(FldName.getInst(), resultFlist.get(FldName.getInst()));
		dealInfo.set(FldPoid.getInst(), resultFlist.get(FldPoid.getInst()));
		//dealInfo.set(FldStartT.getInst(), new Date());
		dealInfo.set(FldStartT.getInst(), resultFlist.get(FldStartT.getInst()));
		dealInfo.set(FldEndT.getInst(), resultFlist.get(FldEndT.getInst()));
		dealInfo.set(FldFlags.getInst(), resultFlist.get(FldFlags.getInst()));		
		dealInfo.set(FldDescr.getInst(), resultFlist.get(FldDescr.getInst()));
		
		
		PurchaseOverride overrideInfo = pInfo.getPurchaseOveride();
		
		if (overrideInfo != null) {
			log.info("Original Start Date --> " +overrideInfo.getStartTime());
			log.info("Original End Date --> " +overrideInfo.getEndTime());
			Date currentDate = new Date();
			if(overrideInfo.getStartTime() != null) {
				
				try {
					//givenDate = format.parse(overrideInfo.getStartTime());
					//startDate = new Date(givenDate.getTime() - new Date().getTime());	
					
					/* Commented because of issue with DateTimeFormatter in AWS
					LocalDateTime startDate_sfdc = LocalDateTime.parse(overrideInfo.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); 
					startDate_str = startDate_sfdc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss.SSS a"));
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS a");					
					startDate = sdf.parse(startDate_str);
					*/
					long start_time = overrideInfo.getStartTime().getTime() - currentDate.getTime();
					if (start_time < 0 && start_time > -60000)
						start_time = 0;
					startDate = new Date(start_time);
					log.info("Start Date --> " +startDate);
					//dealInfo.set(FldStartT.getInst(), startDate);
					
						
				} catch (Exception ex) {
					throw new BRMException("startTime Parse Error", ErrorCodes.ERR_UNKNOWN_ERROR);
				}
				
			}
			
			if(overrideInfo.getEndTime() != null) {
				try {				
					//givenDate = format.parse(overrideInfo.getEndTime());
					//endDate = new Date(givenDate.getTime() - new Date().getTime());
					
					/* Commented because of issue with DateTimeFormatter in AWS
					LocalDateTime endDate_sfdc = LocalDateTime.parse(overrideInfo.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); 
					endDate_str = endDate_sfdc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss.S a"));
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.S a");					
					endDate = sdf.parse(endDate_str);
					*/
					long end_time = overrideInfo.getEndTime().getTime() - currentDate.getTime();
					endDate = new Date(end_time);
					log.info("End Date --> " +endDate);
					//dealInfo.set(FldEndT.getInst(), endDate);
				} catch (Exception ex) {
					throw new BRMException("endTime Parse Error", ErrorCodes.ERR_UNKNOWN_ERROR);
				}
				
			}
		}
		
		
		if(resultFlist.containsKey(FldProducts.getInst())) {
			SparseArray prodArray = resultFlist.get(FldProducts.getInst());			
			Enumeration <?> products = prodArray.getValueEnumerator();
			while (products.hasMoreElements()) {
				FList prodFlist = (FList)products.nextElement();
				
				if (overrideInfo != null) {
					if(overrideInfo.getCharge() != -1) {
						if(overrideInfo.getChargeType().equalsIgnoreCase("NRC")) {
							prodFlist.set(FldPurchaseFeeAmt.getInst(), 
									new BigDecimal(overrideInfo.getCharge()));
							prodFlist.set(FldStatusFlags.getInst(), 
									PortalConst.PROD_STATUS_FLAGS_OVERRIDE_PURCHASE_FEE);
						} else if(overrideInfo.getChargeType().equalsIgnoreCase("MRC")) {
							prodFlist.set(FldCycleFeeAmt.getInst(), 
									new BigDecimal(overrideInfo.getCharge()));
							prodFlist.set(FldStatusFlags.getInst(), 
									PortalConst.PROD_STATUS_FLAGS_OVERRIDE_CYCLE_FEE);
						}
					}
					
					if(overrideInfo.getQuantity() > 1) {
						prodFlist.set(FldQuantity.getInst(), 
								new BigDecimal(overrideInfo.getQuantity()));
					}
					if(overrideInfo.getStartTime() != null && overrideInfo.getEndTime() == null){
						log.info("*** Perpetual ***");
						prodFlist.set(FldUsageStartT.getInst(), startDate);
						prodFlist.set(FldCycleStartT.getInst(), startDate);
// if the given start_t is less than current time (back dated purchase) then set the purchaseStartT also						
						//if(startDate.getTime() < 0)
							prodFlist.set(FldPurchaseStartT.getInst(), startDate);
							
							prodFlist.set(FldUsageStartDetails.getInst(), 0);
							prodFlist.set(FldUsageEndDetails.getInst(), 2);
							prodFlist.set(FldCycleStartDetails.getInst(), 0);
							prodFlist.set(FldCycleEndDetails.getInst(), 2);
							prodFlist.set(FldPurchaseStartDetails.getInst(), 0);
							prodFlist.set(FldPurchaseEndDetails.getInst(), 2);	
					}
					if(overrideInfo.getEndTime() != null) {
						log.info("*** Term ***");
						prodFlist.set(FldUsageStartT.getInst(), startDate);
						prodFlist.set(FldCycleStartT.getInst(), startDate);
						prodFlist.set(FldPurchaseStartT.getInst(), startDate);
						
						prodFlist.set(FldPurchaseEndT.getInst(), endDate);
						prodFlist.set(FldUsageEndT.getInst(), endDate);
						prodFlist.set(FldCycleEndT.getInst(), endDate);
						
						prodFlist.set(FldUsageStartDetails.getInst(), 0);
						prodFlist.set(FldUsageEndDetails.getInst(), 0);
						prodFlist.set(FldCycleStartDetails.getInst(), 0);
						prodFlist.set(FldCycleEndDetails.getInst(), 0);
						prodFlist.set(FldPurchaseStartDetails.getInst(), 0);
						prodFlist.set(FldPurchaseEndDetails.getInst(), 0);
												
					}
				}
				if(! pInfo.isActive()) {
					int statusFlags = prodFlist.get(FldStatusFlags.getInst()) + PortalConst.STATUS_FLAG_ACTIVATE;
					prodFlist.set(FldStatus.getInst(), PortalEnums.PinProductStatus.INACTIVE);
					prodFlist.set(FldStatusFlags.getInst(), statusFlags);
				}
				dealInfo.setElement(FldProducts.getInst(), 0, prodFlist);
			}
			
		}
		
		if(resultFlist.containsKey(FldDiscounts.getInst())) {
			SparseArray discArray = resultFlist.get(FldDiscounts.getInst());			
			Enumeration <?> discounts = discArray.getValueEnumerator();
			while (discounts.hasMoreElements()) {
				FList discFlist = (FList)discounts.nextElement();
				
				if (overrideInfo != null) {
					
					if(overrideInfo.getQuantity() > 1) {
						discFlist.set(FldQuantity.getInst(), 
								new BigDecimal(overrideInfo.getQuantity()));
					}
					
					if(overrideInfo.getStartTime() != null && overrideInfo.getEndTime() == null){
						log.info("*** Perpetual ***");
						discFlist.set(FldUsageStartT.getInst(), startDate);
						discFlist.set(FldCycleStartT.getInst(), startDate);
// if the given start_t is less than current time (back dated purchase) then set the purchaseStartT also						
						//if(startDate.getTime() < 0)
							discFlist.set(FldPurchaseStartT.getInst(), startDate);
							
							discFlist.set(FldUsageStartDetails.getInst(), 0);
							discFlist.set(FldUsageEndDetails.getInst(), 2);
							discFlist.set(FldCycleStartDetails.getInst(), 0);
							discFlist.set(FldCycleEndDetails.getInst(), 2);
							discFlist.set(FldPurchaseStartDetails.getInst(), 0);
							discFlist.set(FldPurchaseEndDetails.getInst(), 2);	
					}
					if(overrideInfo.getEndTime() != null) {
						log.info("*** Term ***");						
						discFlist.set(FldUsageStartT.getInst(), startDate);
						discFlist.set(FldCycleStartT.getInst(), startDate);
						discFlist.set(FldPurchaseStartT.getInst(), startDate);
						discFlist.set(FldPurchaseEndT.getInst(), endDate);
						discFlist.set(FldUsageEndT.getInst(), endDate);
						discFlist.set(FldCycleEndT.getInst(), endDate);
						
						discFlist.set(FldUsageStartDetails.getInst(), 0);
						discFlist.set(FldUsageEndDetails.getInst(), 0);
						discFlist.set(FldCycleStartDetails.getInst(), 0);
						discFlist.set(FldCycleEndDetails.getInst(), 0);
						discFlist.set(FldPurchaseStartDetails.getInst(), 0);
						discFlist.set(FldPurchaseEndDetails.getInst(), 0);	
					}
					
				}
				
				if(! pInfo.isActive()) {
					discFlist.set(FldStatus.getInst(), PortalEnums.PinProductStatus.INACTIVE);
					discFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_ACTIVATE);
				}
				dealInfo.setElement(FldDiscounts.getInst(), 0, discFlist);
			}
		}
		
		purchaseFlist.set(FldDealInfo.getInst(), dealInfo);
		log.info("the deal purchase flist" +purchaseFlist);
		FList flistOutput = connector.callOpcode(PortalOp.SUBSCRIPTION_PURCHASE_DEAL, purchaseFlist, "Subscription - Purchase Deal");
		log.info("the deal purchase output" +flistOutput);
		if (! flistOutput.containsKey(FldPoid.getInst())) {
			throw new BRMException("Addon Purchase Error", ErrorCodes.ERR_UNKNOWN_ERROR);
		}
		
	}
	
	 
	private void purchaseDeal(PCMWrapper connector, Poid accountPoid, FList dealDetailsFlist) throws BRMException, EBufException {
		FlistCreator creator = new FlistCreator();
		Poid servicePoid;
		Date givenDate = null; 

		
		String permitted = dealDetailsFlist.get(FldPermitted.getInst());
		if (! permitted.equalsIgnoreCase("/account") && ! serviceHash.containsKey(permitted)) {
			log.info("getting the service poid for the type:" +permitted);
			servicePoid = connector.getServicePoidFromType(accountPoid, permitted);
			serviceHash.put(permitted, servicePoid);
		} else {
			log.info("service poid already present for the type:" +permitted);
			servicePoid = serviceHash.get(permitted);
		}
		log.info("the service poid is:" +servicePoid);
		
		
		FList purchaseFlist = new FList();
		purchaseFlist.set(FldPoid.getInst(), accountPoid);
		
		if(! permitted.equalsIgnoreCase("/account")) 
			purchaseFlist.set(FldServiceObj.getInst(), (Poid)serviceHash.get(permitted));
			
		
		purchaseFlist.set(FldProgramName.getInst(), "Subscription - PurchaseDeal");
		
		FList dealInfo = new FList();
		dealInfo.set(FldName.getInst(), dealDetailsFlist.get(FldName.getInst()));
		dealInfo.set(FldPoid.getInst(), dealDetailsFlist.get(FldPoid.getInst()));
		dealInfo.set(FldStartT.getInst(), dealDetailsFlist.get(FldStartT.getInst()));
		dealInfo.set(FldEndT.getInst(), dealDetailsFlist.get(FldEndT.getInst()));
		dealInfo.set(FldFlags.getInst(), dealDetailsFlist.get(FldFlags.getInst()));		
		dealInfo.set(FldDescr.getInst(), dealDetailsFlist.get(FldDescr.getInst()));
		
		
		/*
		if(dealDetailsFlist.containsKey(FldProducts.getInst())) {
			SparseArray prodArray = dealDetailsFlist.get(FldProducts.getInst());			
			Enumeration <?> products = prodArray.getValueEnumerator();
			while (products.hasMoreElements()) {
				FList prodFlist = (FList)products.nextElement();
				
			
					if(startDate != null){
						prodFlist.set(FldUsageStartT.getInst(), startDate);
						prodFlist.set(FldCycleStartT.getInst(), startDate);
							prodFlist.set(FldPurchaseStartT.getInst(), startDate);
					}
					if(endDate != null) {
						prodFlist.set(FldPurchaseEndT.getInst(), endDate);
						prodFlist.set(FldUsageEndT.getInst(), endDate);
						prodFlist.set(FldCycleEndT.getInst(), endDate);
					
					}
					dealInfo.setElement(FldProducts.getInst(), 0, prodFlist);
		
			}

					//dealInfo.set(FldProducts.getInst(), prodArray);
		}
		
		if(dealDetailsFlist.containsKey(FldDiscounts.getInst())) {
			SparseArray discArray = dealDetailsFlist.get(FldDiscounts.getInst());				
			Enumeration <?> discounts = discArray.getValueEnumerator();
			while (discounts.hasMoreElements()) {
				FList discFlist = (FList)discounts.nextElement();
					
					if(startDate != null){
						discFlist.set(FldUsageStartT.getInst(), startDate);
						discFlist.set(FldCycleStartT.getInst(), startDate);
							discFlist.set(FldPurchaseStartT.getInst(), startDate);
					}
					if(endDate != null) {
						discFlist.set(FldPurchaseEndT.getInst(), endDate);
						discFlist.set(FldUsageEndT.getInst(), endDate);
						discFlist.set(FldCycleEndT.getInst(), endDate);
					}
					dealInfo.setElement(FldDiscounts.getInst(), 0, discFlist);
					
				}
				

			//dealInfo.set(FldDiscounts.getInst(), discArray);
		}
		*/
		
		
		if(dealDetailsFlist.containsKey(FldProducts.getInst())) {
			SparseArray prodArray = dealDetailsFlist.get(FldProducts.getInst());

					dealInfo.set(FldProducts.getInst(), prodArray);
		}
		
		if(dealDetailsFlist.containsKey(FldDiscounts.getInst())) {
			SparseArray discArray = dealDetailsFlist.get(FldDiscounts.getInst());			

			dealInfo.set(FldDiscounts.getInst(), discArray);
		}
		
		
		purchaseFlist.set(FldDealInfo.getInst(), dealInfo);
		log.info("the deal purchase flist" +purchaseFlist);
		FList flistOutput = connector.callOpcode(PortalOp.SUBSCRIPTION_PURCHASE_DEAL, purchaseFlist, "Subscription - Purchase Deal");
		log.info("the deal purchase output" +flistOutput);
		if (! flistOutput.containsKey(FldPoid.getInst())) {
			throw new BRMException("Addon Purchase Error", ErrorCodes.ERR_UNKNOWN_ERROR);
		}
		
	}
	
	public void setProductsActive(PCMWrapper connector, Poid accountPoid) throws BRMException, EBufException {
		
		int newStatuFlags = 0;
		FList pOfferFlist = new FList();
		pOfferFlist.set(FldPoid.getInst(), accountPoid);
		pOfferFlist.set(FldScopeObj.getInst(), accountPoid);
		pOfferFlist.set(FldStatusFlags.getInst(), PortalEnums.PinProductStatus.INACTIVE);
		FList productsFlist = new FList();
		productsFlist.set(FldStatus.getInst());
		productsFlist.set(FldStatusFlags.getInst());
		productsFlist.set(FldOfferingObj.getInst());
		productsFlist.set(FldProductObj.getInst());
		pOfferFlist.setElement(FldProducts.getInst(), 0, productsFlist);
		
		FList discountsFlist = new FList();
		discountsFlist.set(FldStatus.getInst());
		discountsFlist.set(FldStatusFlags.getInst());
		discountsFlist.set(FldOfferingObj.getInst());
		discountsFlist.set(FldDiscountObj.getInst());
		pOfferFlist.setElement(FldDiscounts.getInst(), 0, discountsFlist);
		
		FList flistOutput = connector.callOpcode(PortalOp.SUBSCRIPTION_GET_PURCHASED_OFFERINGS, 
							pOfferFlist, "Subscription - get_purchased_offerings");
		log.info("the ouput flist is" +flistOutput);
		
		FList setInfoFlist;
		FList setStatusFList;
		FList outputFlist;
		
		if(flistOutput.containsKey(FldProducts.getInst())) {
			SparseArray prodArray = flistOutput.get(FldProducts.getInst());			
			Enumeration <?> products = prodArray.getValueEnumerator();
			while (products.hasMoreElements()) {
				FList prodFlist = (FList)products.nextElement();
				newStatuFlags = prodFlist.get(FldStatusFlags.getInst()) - PortalConst.STATUS_FLAG_ACTIVATE;
				setInfoFlist = new FList();
				setInfoFlist.set(FldPoid.getInst(), accountPoid);
				setInfoFlist.set(FldServiceObj.getInst(), prodFlist.get(FldServiceObj.getInst()));
				setInfoFlist.set(FldProgramName.getInst(), "setProductsActive-set_prodinfo");
				
				productsFlist = new FList();
				productsFlist.set(FldStatusFlags.getInst(), newStatuFlags);
				productsFlist.set(FldOfferingObj.getInst(), prodFlist.get(FldOfferingObj.getInst()));
				productsFlist.set(FldProductObj.getInst(), prodFlist.get(FldProductObj.getInst()));
				productsFlist.set(FldPurchaseStartT.getInst(), new Date());
				productsFlist.set(FldCycleStartT.getInst(), new Date());
				productsFlist.set(FldUsageStartT.getInst(), new Date());
				setInfoFlist.setElement(FldProducts.getInst(), 0, productsFlist);
				
				log.info("the set_prodinfo inflist" + setInfoFlist);
				outputFlist = connector.callOpcode(PortalOp.SUBSCRIPTION_SET_PRODINFO, 
										setInfoFlist, "subscription_set_prodinfo");
				log.info("the set_prodinfo outflist" + outputFlist);
				
				setStatusFList = new FList();
				setStatusFList.set(FldPoid.getInst(), accountPoid);
				setStatusFList.set(FldProgramName.getInst(), "setProductsActive-set_product_status");
							
				FList statusesFlist = new FList();
				statusesFlist.set(FldStatus.getInst(), PortalEnums.PinProductStatus.ACTIVE);
				statusesFlist.set(FldStatusFlags.getInst(), newStatuFlags);
				statusesFlist.set(FldOfferingObj.getInst(), prodFlist.get(FldOfferingObj.getInst()));
				setStatusFList.setElement(FldStatuses.getInst(), 0, statusesFlist);
				
				log.info("the set_product_status inflist" + setStatusFList);
				outputFlist = connector.callOpcode(PortalOp.SUBSCRIPTION_SET_PRODUCT_STATUS, 
									setStatusFList, "subscription_set_product_status");
				log.info("the set_product_status outflist" + outputFlist);
			}
				
		}	
		
		if(flistOutput.containsKey(FldDiscounts.getInst())) {
			SparseArray discArray = flistOutput.get(FldDiscounts.getInst());			
			Enumeration <?> discounts = discArray.getValueEnumerator();
			while (discounts.hasMoreElements()) {
				FList discFlist = (FList)discounts.nextElement();
				newStatuFlags = discFlist.get(FldStatusFlags.getInst()) - PortalConst.STATUS_FLAG_ACTIVATE;
				setInfoFlist = new FList();
				setInfoFlist.set(FldPoid.getInst(), accountPoid);
				setInfoFlist.set(FldServiceObj.getInst(), discFlist.get(FldServiceObj.getInst()));
				setInfoFlist.set(FldProgramName.getInst(), "setProductsActive-set_discountinfo");
				discountsFlist = new FList();
				discountsFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_ACTIVATE);
				discountsFlist.set(FldOfferingObj.getInst(), discFlist.get(FldOfferingObj.getInst()));
				discountsFlist.set(FldDiscountObj.getInst(), discFlist.get(FldDiscountObj.getInst()));
				discountsFlist.set(FldPurchaseStartT.getInst(), new Date(0));
				discountsFlist.set(FldCycleStartT.getInst(), new Date(0));
				discountsFlist.set(FldUsageStartT.getInst(), new Date(0));
				setInfoFlist.setElement(FldDiscounts.getInst(), 0, discountsFlist);

				log.info("the set_discountinfo inflist" + setInfoFlist);
				outputFlist = connector.callOpcode(PortalOp.SUBSCRIPTION_SET_DISCOUNTINFO, 
										setInfoFlist, "subscription_set_discountinfo");
				log.info("the set_discountinfo outflist" + outputFlist);
				
				setStatusFList = new FList();
				setStatusFList.set(FldPoid.getInst(), accountPoid);
				setStatusFList.set(FldProgramName.getInst(), "setProductsActive-set_discount_status");
				
				FList statusesFlist = new FList();
				statusesFlist.set(FldStatus.getInst(), PortalEnums.PinDiscountStatus.ACTIVE);
				statusesFlist.set(FldStatusFlags.getInst(), newStatuFlags);
				statusesFlist.set(FldOfferingObj.getInst(), discFlist.get(FldOfferingObj.getInst()));
				setStatusFList.setElement(FldStatuses.getInst(), 0, statusesFlist);
				
				log.info("the set_discount_status inflist" + setStatusFList);
				outputFlist = connector.callOpcode(PortalOp.SUBSCRIPTION_SET_DISCOUNT_STATUS, 
									setStatusFList, "subscription_set_discount_status");
				log.info("the set_discount_status outflist" + outputFlist);
			}
				
		}
		
	}
	
	public Response cancelDealWorker(DealInfo dInfo) {
        
        log.info("Deal Cancellation request got");
        log.info("for account no " +dInfo.getAccountNo());
        List<ProdInfo> dealList  = dInfo.getProdInfo();
       
        PCMWrapper connector = new PCMWrapper();
        Response response = new Response();
        Poid accountPoid = null;
        try {
                    connector.createContext();
                    accountPoid = connector.getAccountPoidFromAccountNo(dInfo.getAccountNo());                                 
                    connector.openTransaction(accountPoid);
                    for (ProdInfo pInfo : dealList) {
                                cancelDeal(connector, accountPoid, pInfo);
                    }
                    connector.commitTransaction(accountPoid);
                    response.setResponseMsg("SUCCESS");
                    response.setResponseCode(ErrorCodes.SUCCESS);
        } catch (BRMException ex) {
                    log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
                    log.error(ex.getHiddenException());
                    log.error(BRMException.getStackTraceAsString(ex));
                    response.setResponseMsg(ex.getMessage());
                    response.setResponseCode(ex.getErrorCode());
                    
                    log.info("Connector completes processing for SubscriptionDao - cancelDealWorker");
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
                               
        } catch (Exception ex) {
                    log.error(BRMException.getStackTraceAsString(ex));
                    response.setResponseMsg(ex.getMessage());
                    response.setResponseCode(1);
                    
                    log.info("Connector completes processing for SubscriptionDao - cancelDealWorker");
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
        } finally {
        	/*
                    log.info("Connector completes processing for SubscriptionDao - cancelDealWorker");
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
                                            
        }         */                         
        }                     
        return response;
}

	private void cancelDeal(PCMWrapper connector, Poid accountPoid, ProdInfo pInfo) throws BRMException, EBufException {
		
		FlistCreator creator = new FlistCreator();
		Poid servicePoid;
		FList dealInFlist = creator.getPoidFromStrField("/deal",
		                        FldName.getInst(), pInfo.getName());
		FList dealOutFlist = connector.search(dealInFlist, 256, "Search Deal by Name");
		FList resultFlist = dealOutFlist.getElement(FldResults.getInst(), 0);

		String permitted = resultFlist.get(FldPermitted.getInst());
		
		if (! permitted.equalsIgnoreCase("/account") && ! serviceHash.containsKey(permitted)) {
			servicePoid = connector.getServicePoidFromType(accountPoid, permitted);
			serviceHash.put(permitted, servicePoid);
		} else {
			servicePoid = serviceHash.get(permitted);
		}

		FList pkgInFlist;
		pkgInFlist = creator.getPackageId("/purchased_product", 
						accountPoid, resultFlist.get(FldPoid.getInst()), 3);
		pkgInFlist.set(FldPoid.getInst(), new Poid(connector.getDb(), connector.getId(), "/search"));
		pkgInFlist.set(FldFlags.getInst(), 256);
		FList pkgOutFlist;
		
		log.info("the prod flist:" +pkgInFlist );
		
		pkgOutFlist = connector.callOpcode(PortalOp.SEARCH, pkgInFlist, "package_id from purchased_product");
		if(! pkgOutFlist.containsKey(FldResults.getInst())) {
			
			pkgInFlist.set(FldTemplate.getInst(),
					  " select X from /purchased_discount where F1 = V1 and F2 = V2 and F3 != V3 ");
			log.info("the disc flist:" +pkgInFlist );
			pkgOutFlist = connector.callOpcode(PortalOp.SEARCH, pkgInFlist, "package_id from purchased_discount");

			if(! pkgOutFlist.containsKey(FldResults.getInst())) {
				throw new BRMException("Addon not purchased/active for the account", ErrorCodes.ERR_PACKAGE_ID_NOT_FOUND);
			}
		}

		
		FList pkgResultFlist = pkgOutFlist.getElement(FldResults.getInst(), 0);
		
		FList cancelFlist = new FList();
		cancelFlist.set(FldPoid.getInst(), accountPoid);
		if(! permitted.equalsIgnoreCase("/account"))
			cancelFlist.set(FldServiceObj.getInst(), (Poid)serviceHash.get(permitted));
		   
		cancelFlist.set(FldProgramName.getInst(), "Subscription - CancelDeal");
		   
		FList dealInfo = new FList();
		dealInfo.set(FldPackageId.getInst(), pkgResultFlist.get(FldPackageId.getInst()));
		dealInfo.set(FldDealObj.getInst(), resultFlist.get(FldPoid.getInst()));
		
		cancelFlist.set(FldDealInfo.getInst(), dealInfo);
		log.info("the deal cancellation flist" +cancelFlist);
		    
		FList flistOutput = connector.callOpcode(PortalOp.SUBSCRIPTION_CANCEL_DEAL, cancelFlist, "Subscription - Cancel Deal");
		log.info("the deal cancellation output" +flistOutput);
		if (! flistOutput.containsKey(FldPoid.getInst())) {
			throw new BRMException("Addon Cancel Error", ErrorCodes.ERR_UNKNOWN_ERROR);
		}
		       
	}
	
	/*
	private void cancelDeal(PCMWrapper connector, Poid accountPoid, Poid pInfo, String permitted) throws BRMException, EBufException {
		
		FlistCreator creator = new FlistCreator();
		
		Poid servicePoid;

		if (! permitted.equalsIgnoreCase("/account") && ! serviceHash.containsKey(permitted)) {
			servicePoid = connector.getServicePoidFromType(accountPoid, permitted);
			serviceHash.put(permitted, servicePoid);
		} else {
			servicePoid = serviceHash.get(permitted);
		}
		

		FList pkgInFlist;
		pkgInFlist = creator.getPackageId("/purchased_product", 
						accountPoid, pInfo, 3);
		pkgInFlist.set(FldPoid.getInst(), new Poid(connector.getDb(), connector.getId(), "/search"));
		pkgInFlist.set(FldFlags.getInst(), 256);
		FList pkgOutFlist;
		
		log.info("the prod flist:" +pkgInFlist );
		
		pkgOutFlist = connector.callOpcode(PortalOp.SEARCH, pkgInFlist, "package_id from purchased_product");
		if(! pkgOutFlist.containsKey(FldResults.getInst())) {
			
			pkgInFlist.set(FldTemplate.getInst(),
					  " select X from /purchased_discount where F1 = V1 and F2 = V2 and F3 != V3 ");
			log.info("the disc flist:" +pkgInFlist );
			pkgOutFlist = connector.callOpcode(PortalOp.SEARCH, pkgInFlist, "package_id from purchased_discount");

			if(! pkgOutFlist.containsKey(FldResults.getInst())) {
				throw new BRMException("Addon not purchased/active for the account", ErrorCodes.ERR_PACKAGE_ID_NOT_FOUND);
			}
		}

		
		FList pkgResultFlist = pkgOutFlist.getElement(FldResults.getInst(), 0);
		
		FList cancelFlist = new FList();
		cancelFlist.set(FldPoid.getInst(), accountPoid);
		
		if(! permitted.equalsIgnoreCase("/account"))
			cancelFlist.set(FldServiceObj.getInst(), (Poid)serviceHash.get(permitted));
			
		   
		cancelFlist.set(FldProgramName.getInst(), "Subscription - CancelDeal");
		   
		FList dealInfo = new FList();
		dealInfo.set(FldPackageId.getInst(), pkgResultFlist.get(FldPackageId.getInst()));
		dealInfo.set(FldDealObj.getInst(), pInfo);
		
		cancelFlist.set(FldDealInfo.getInst(), dealInfo);
		log.info("the deal cancellation flist" +cancelFlist);
		    
		FList flistOutput = connector.callOpcode(PortalOp.SUBSCRIPTION_CANCEL_DEAL, cancelFlist, "Subscription - Cancel Deal");
		log.info("the deal cancellation output" +flistOutput);
		if (! flistOutput.containsKey(FldPoid.getInst())) {
			throw new BRMException("Addon Cancel Error", ErrorCodes.ERR_UNKNOWN_ERROR);
		}
		       
	}	
	*/
	
	private void cancelDeal(PCMWrapper connector, Poid accountPoid, Poid pInfo, String permitted, String dealName) throws BRMException, EBufException {
		FlistCreator creator = new FlistCreator();
		Poid servicePoid;


		Poid dealPoid = pInfo;
		log.info("*** deal poid is: " +dealPoid+" ***");
		if (!permitted.equalsIgnoreCase("/account") && !serviceHash.containsKey(permitted)) {
			 log.info("getting the service poid for the type:" +permitted);
			servicePoid = connector.getServicePoidFromType(accountPoid, permitted);
			serviceHash.put(permitted, servicePoid);
		} else {
			log.info("service poid already present for the type:" +permitted);
			servicePoid = serviceHash.get(permitted);
		}
		log.info("the service poid is:" +servicePoid);
		FList cancelFlist = new FList();
		cancelFlist.set(FldPoid.getInst(), accountPoid);
		if (!permitted.equalsIgnoreCase("/account"))
			cancelFlist.set(FldServiceObj.getInst(), (Poid) serviceHash.get(permitted));

		cancelFlist.set(FldProgramName.getInst(), "Subscription - CancelDeal");
		
		//Create PCM_OP_SEARCH Flist to query purshased product id from account id and deal id
		FList purchasedProdFlist = new FList();
		FList result_Flist = new FList();
		FList argsFlist1 = new FList();
		FList argsFlist2 = new FList();
		purchasedProdFlist.set(FldTemplate.getInst(),
				  " select X from /purchased_product where F1 = V1 AND F2 = V2 ");
		argsFlist1.set(FldAccountObj.getInst(), accountPoid);
		argsFlist2.set(FldDealObj.getInst(), dealPoid);
		purchasedProdFlist.setElement(FldArgs.getInst(), 1, argsFlist1);
		purchasedProdFlist.setElement(FldArgs.getInst(), 2, argsFlist2);
		purchasedProdFlist.set(FldFlags.getInst(), 0);
		Poid searchPoid = new Poid(1,-1,"/search");
		
		purchasedProdFlist.set(FldPoid.getInst(), searchPoid);
		purchasedProdFlist.setElement(FldResults.getInst(), 1, result_Flist); //set for result
		log.info("*** Request Flist of Search Purshased Product ***");
		log.info(purchasedProdFlist);
		log.info("******");
		FList purchasedProdOutFlist = connector.search(purchasedProdFlist, 256, "Search Purchased Product");
		
		log.info("*** Response Flist of Search Purshased Product ***");
		log.info(purchasedProdOutFlist);
		log.info("******");
		

		FList resultFList = purchasedProdOutFlist.getElement(FldResults.getInst(), 0);
		int packageId = resultFList.get(FldPackageId.getInst());
		log.info("package id is:" +packageId);

		FList dealInfo = new FList();
		dealInfo.set(FldName.getInst(), dealName);
		dealInfo.set(FldPackageId.getInst(), packageId);
		dealInfo.set(FldPoid.getInst(), dealPoid);

		cancelFlist.set(FldDealInfo.getInst(), dealInfo);
		log.info("the deal cancellation flist" + cancelFlist);
		FList flistOutput = connector.callOpcode(PortalOp.SUBSCRIPTION_CANCEL_DEAL, cancelFlist,
				"Subscription - Cancel Deal");
		log.info("the deal cancellation output" + flistOutput);
		if (!flistOutput.containsKey(FldPoid.getInst())) {
			throw new BRMException("Addon Purchase Error", ErrorCodes.ERR_UNKNOWN_ERROR);
		}

	}
	
	public Response changeDealWorker(PlanChangeInfo dInfo) {
        
        log.info("Plan Change request got");
        log.info("for account no " +dInfo.getAccountNo());
        List<ProdInfo> oldPlanList  = dInfo.getOldProdInfo();
        List<ProdInfo> newPlanList  = dInfo.getNewProdInfo();
       
        PCMWrapper connector = new PCMWrapper();
        Response response = new Response();
        Poid accountPoid = null;
        try {
                    connector.createContext();
                    accountPoid = connector.getAccountPoidFromAccountNo(dInfo.getAccountNo());                                 
                    connector.openTransaction(accountPoid);
                    
                    //First deactivate the current plan
                    for (ProdInfo pInfo : oldPlanList) {
                    	
                                //cancelDeal(connector, accountPoid, pInfo);
                    	
                    	//Search all deals which are part of this plan
                    	log.info("Search all deals which are part of this plan - START");
                		//Create PCM_OP_SEARCH Flist to query all deals associated to the plan
                		FList inpFlist = new FList();
                		FList result_Flist = new FList();
                		FList argsFlist1 = new FList();
                		
                		inpFlist.set(FldTemplate.getInst(),
                				  " select X from /plan where F1 = V1 ");
                		argsFlist1.set(FldName.getInst(), pInfo.getName());
                		
                		inpFlist.setElement(FldArgs.getInst(), 1, argsFlist1);
                		
                		inpFlist.set(FldFlags.getInst(), 0);
                		Poid searchPoid = new Poid(1,-1,"/search");
                		
                		inpFlist.set(FldPoid.getInst(), searchPoid);
                		inpFlist.setElement(FldResults.getInst(), 1, result_Flist); //set for result
                		log.info("*** Request Flist of Search Deals ***");
                		log.info(inpFlist);
                		log.info("******");
                		FList outFlist = connector.search(inpFlist, 256, "Search Deals");
                		
                		log.info("*** Response Flist of Search Deals ***");
                		log.info(outFlist);
                		log.info("******");                 		
                		log.info("Search all deals which are part of this plan - END");
                		
                		FList resultFList = outFlist.getElement(FldResults.getInst(), 0);
                		
                		if (resultFList.containsKey(FldServices.getInst())){
                			SparseArray servicesArray = resultFList.get(FldServices.getInst());
                			Enumeration<?> services = servicesArray.getValueEnumerator();
                			while (services.hasMoreElements()) {
                		FList servicesFlist = (FList) services.nextElement();
                		
                		if (servicesFlist.containsKey(FldDealObj.getInst())){
            				Poid dealObj = servicesFlist.get(FldDealObj.getInst());
            				log.info("Deal Obj is:" +dealObj);
            				FList dealDetailsFlist = connector.getDealDetails(dealObj); //Get details of Deal                    		
            				String permitted = dealDetailsFlist.get(FldPermitted.getInst());
            				String dealName = dealDetailsFlist.get(FldName.getInst());
            				cancelDeal(connector, accountPoid, dealObj,permitted,dealName);
                		}
                		
                		if (servicesFlist.containsKey(FldDeals.getInst())){
                			SparseArray dealsArray = servicesFlist.get(FldDeals.getInst());
                			Enumeration<?> deals = dealsArray.getValueEnumerator();
                			while (deals.hasMoreElements()) {
                				FList dealFlist = (FList) deals.nextElement();
                				Poid dealObj = dealFlist.get(FldDealObj.getInst());
                        		
                        		FList dealDetailsFlist = connector.getDealDetails(dealObj); //Get details of Deal
                        		
                				String permitted = dealDetailsFlist.get(FldPermitted.getInst());
                				String dealName = dealDetailsFlist.get(FldName.getInst());
                				log.info("Deal Obj is:" +dealObj);
                				cancelDeal(connector, accountPoid, dealObj,permitted,dealName);
                			}
                		}
                		

                		
                    }
                		}}
                    log.info("Plan Change - Deactivation of current plan successful");
                    
                    //Then purchase the new plan
        			for (ProdInfo pInfo : newPlanList) {
        				//purchaseDeal(connector, accountPoid, pInfo);
        				
                    	//Search all deals which are part of this plan
                    	log.info("Search all deals which are part of this plan - START");
                		//Create PCM_OP_SEARCH Flist to query all deals associated to the plan
                		FList inpFlist = new FList();
                		FList result_Flist = new FList();
                		FList argsFlist1 = new FList();
                		
                		inpFlist.set(FldTemplate.getInst(),
                				  " select X from /plan where F1 = V1 ");
                		argsFlist1.set(FldName.getInst(), pInfo.getName());
                		
                		inpFlist.setElement(FldArgs.getInst(), 1, argsFlist1);
                		
                		inpFlist.set(FldFlags.getInst(), 0);
                		Poid searchPoid = new Poid(1,-1,"/search");
                		
                		inpFlist.set(FldPoid.getInst(), searchPoid);
                		inpFlist.setElement(FldResults.getInst(), 1, result_Flist); //set for result
                		log.info("*** Request Flist of Search Deals ***");
                		log.info(inpFlist);
                		log.info("******");
                		FList outFlist = connector.search(inpFlist, 256, "Search Deals");
                		
                		log.info("*** Response Flist of Search Deals ***");
                		log.info(outFlist);
                		log.info("******");      	
                		log.info("Search all deals which are part of this plan - END");
                		
                		FList resultFList = outFlist.getElement(FldResults.getInst(), 0);
                		FList servicesFlist = resultFList.getElement(FldServices.getInst(), 0);
                		
                		Date startDate = null;
                		Date endDate = null;
                		if(dInfo.getStartDate() != null && !dInfo.getStartDate().toString().isEmpty()){
                			Date currentDate = new Date();
                			long start_time = dInfo.getStartDate().getTime() - currentDate.getTime();
                			if (start_time < 0 && start_time > -60000)
                				start_time = 0;
            				startDate = new Date(start_time);
            				if(dInfo.getEndDate() != null && !dInfo.getEndDate().toString().isEmpty()){
            					long end_time = dInfo.getEndDate().getTime() - currentDate.getTime();
            					endDate = new Date(end_time);
            				}
            				
                		}
                		
                		
                		if (servicesFlist.containsKey(FldDealObj.getInst())){
            				Poid dealObj = servicesFlist.get(FldDealObj.getInst());
            				log.info("Deal Obj is:" +dealObj);
            				FList dealDetailsFlist = connector.getDealDetails(dealObj); //Get details of Deal
            				log.info("Deal Details Flist:" +dealDetailsFlist);
            				

            				//FList dealDetailsUpdated = new FList();
            				if(dealDetailsFlist.containsKey(FldProducts.getInst())) {
            					SparseArray prodArray = dealDetailsFlist.get(FldProducts.getInst());	
            					//SparseArray prodArray1 = new SparseArray();
            					Enumeration <?> products = prodArray.getValueEnumerator();
            					while (products.hasMoreElements()) {
            						FList prodFlist = (FList)products.nextElement();
            						
            					
            							if(startDate != null && endDate == null){
            								log.info("*** Perpetual ***");
            								prodFlist.set(FldUsageStartT.getInst(), startDate);
            								prodFlist.set(FldCycleStartT.getInst(), startDate);
            								prodFlist.set(FldPurchaseStartT.getInst(), startDate);
            								
            								prodFlist.set(FldUsageStartDetails.getInst(), 0);
            								prodFlist.set(FldUsageEndDetails.getInst(), 2);
            								prodFlist.set(FldCycleStartDetails.getInst(), 0);
            								prodFlist.set(FldCycleEndDetails.getInst(), 2);
            								prodFlist.set(FldPurchaseStartDetails.getInst(), 0);
            								prodFlist.set(FldPurchaseEndDetails.getInst(), 2);
            							}
            							if(startDate != null && endDate != null) {
            								log.info("*** Term ***");
            								prodFlist.set(FldUsageStartT.getInst(), startDate);
            								prodFlist.set(FldCycleStartT.getInst(), startDate);
            								prodFlist.set(FldPurchaseStartT.getInst(), startDate);
            								prodFlist.set(FldPurchaseEndT.getInst(), endDate);
            								prodFlist.set(FldUsageEndT.getInst(), endDate);
            								prodFlist.set(FldCycleEndT.getInst(), endDate);
            								
            								prodFlist.set(FldUsageStartDetails.getInst(), 0);
            								prodFlist.set(FldUsageEndDetails.getInst(), 0);
            								prodFlist.set(FldCycleStartDetails.getInst(), 0);
            								prodFlist.set(FldCycleEndDetails.getInst(), 0);
            								prodFlist.set(FldPurchaseStartDetails.getInst(), 0);
            								prodFlist.set(FldPurchaseEndDetails.getInst(), 0);
            							
            							}
            							
            							//prodArray1.add(prodFlist);
            				
            					}

            							//dealDetailsUpdated.set(FldProducts.getInst(), prodArray1);
            				}
            				
            				if(dealDetailsFlist.containsKey(FldDiscounts.getInst())) {
            					SparseArray discArray = dealDetailsFlist.get(FldDiscounts.getInst());				
            					Enumeration <?> discounts = discArray.getValueEnumerator();
            					while (discounts.hasMoreElements()) {
            						FList discFlist = (FList)discounts.nextElement();
            							
        							if(startDate != null && endDate == null){
        								log.info("*** Perpetual ***");
        								discFlist.set(FldUsageStartT.getInst(), startDate);
        								discFlist.set(FldCycleStartT.getInst(), startDate);
        								discFlist.set(FldPurchaseStartT.getInst(), startDate);
        								
        								discFlist.set(FldUsageStartDetails.getInst(), 0);
        								discFlist.set(FldUsageEndDetails.getInst(), 2);
        								discFlist.set(FldCycleStartDetails.getInst(), 0);
        								discFlist.set(FldCycleEndDetails.getInst(), 2);
        								discFlist.set(FldPurchaseStartDetails.getInst(), 0);
        								discFlist.set(FldPurchaseEndDetails.getInst(), 2);
        							}
        							if(startDate != null && endDate != null) {
        								log.info("*** Term ***");
        								discFlist.set(FldUsageStartT.getInst(), startDate);
        								discFlist.set(FldCycleStartT.getInst(), startDate);
        								discFlist.set(FldPurchaseStartT.getInst(), startDate);
        								discFlist.set(FldPurchaseEndT.getInst(), endDate);
        								discFlist.set(FldUsageEndT.getInst(), endDate);
        								discFlist.set(FldCycleEndT.getInst(), endDate);
        								
        								discFlist.set(FldUsageStartDetails.getInst(), 0);
        								discFlist.set(FldUsageEndDetails.getInst(), 0);
        								discFlist.set(FldCycleStartDetails.getInst(), 0);
        								discFlist.set(FldCycleEndDetails.getInst(), 0);
        								discFlist.set(FldPurchaseStartDetails.getInst(), 0);
        								discFlist.set(FldPurchaseEndDetails.getInst(), 0);
        							
        							}
            							//dealDetailsUpdated.setElement(FldDiscounts.getInst(), 0, discFlist);
            							
            						}
            						

            					//dealInfo.set(FldDiscounts.getInst(), discArray);
            				}
            				
            				//log.info("Deal Details FList UPDATED");
            				log.info("dealDetailsFlist --> "+dealDetailsFlist);
            				purchaseDeal(connector, accountPoid, dealDetailsFlist);
                		}
                		
                		if (servicesFlist.containsKey(FldDeals.getInst())){
                			SparseArray dealsArray = servicesFlist.get(FldDeals.getInst());
                			Enumeration<?> deals = dealsArray.getValueEnumerator();
                			while (deals.hasMoreElements()) {
                				FList dealFlist = (FList) deals.nextElement();
                				Poid dealObj = dealFlist.get(FldDealObj.getInst());
                				log.info("Deal Obj is:" +dealObj);
                				FList dealDetailsFlist = connector.getDealDetails(dealObj); //Get details of Deal
                				log.info("Deal Details Flist:" +dealDetailsFlist);
                				
                				
                				if(dealDetailsFlist.containsKey(FldProducts.getInst())) {
                					SparseArray prodArray = dealDetailsFlist.get(FldProducts.getInst());	
                					Enumeration <?> products = prodArray.getValueEnumerator();
                					while (products.hasMoreElements()) {
                						FList prodFlist = (FList)products.nextElement();
                						
                					
            							if(startDate != null && endDate == null){
            								log.info("*** Perpetual ***");
            								prodFlist.set(FldUsageStartT.getInst(), startDate);
            								prodFlist.set(FldCycleStartT.getInst(), startDate);
            								prodFlist.set(FldPurchaseStartT.getInst(), startDate);
            								
            								prodFlist.set(FldUsageStartDetails.getInst(), 0);
            								prodFlist.set(FldUsageEndDetails.getInst(), 2);
            								prodFlist.set(FldCycleStartDetails.getInst(), 0);
            								prodFlist.set(FldCycleEndDetails.getInst(), 2);
            								prodFlist.set(FldPurchaseStartDetails.getInst(), 0);
            								prodFlist.set(FldPurchaseEndDetails.getInst(), 2);
            							}
            							if(startDate != null && endDate != null) {
            								log.info("*** Term ***");
            								prodFlist.set(FldUsageStartT.getInst(), startDate);
            								prodFlist.set(FldCycleStartT.getInst(), startDate);
            								prodFlist.set(FldPurchaseStartT.getInst(), startDate);
            								prodFlist.set(FldPurchaseEndT.getInst(), endDate);
            								prodFlist.set(FldUsageEndT.getInst(), endDate);
            								prodFlist.set(FldCycleEndT.getInst(), endDate);
            								
            								prodFlist.set(FldUsageStartDetails.getInst(), 0);
            								prodFlist.set(FldUsageEndDetails.getInst(), 0);
            								prodFlist.set(FldCycleStartDetails.getInst(), 0);
            								prodFlist.set(FldCycleEndDetails.getInst(), 0);
            								prodFlist.set(FldPurchaseStartDetails.getInst(), 0);
            								prodFlist.set(FldPurchaseEndDetails.getInst(), 0);
            							
            							}               							
                							                				
                					}
                							
                				}
                				
                				if(dealDetailsFlist.containsKey(FldDiscounts.getInst())) {
                					SparseArray discArray = dealDetailsFlist.get(FldDiscounts.getInst());				
                					Enumeration <?> discounts = discArray.getValueEnumerator();
                					while (discounts.hasMoreElements()) {
                						FList discFlist = (FList)discounts.nextElement();
                							
            							if(startDate != null && endDate == null){
            								log.info("*** Perpetual ***");
            								discFlist.set(FldUsageStartT.getInst(), startDate);
            								discFlist.set(FldCycleStartT.getInst(), startDate);
            								discFlist.set(FldPurchaseStartT.getInst(), startDate);
            								
            								discFlist.set(FldUsageStartDetails.getInst(), 0);
            								discFlist.set(FldUsageEndDetails.getInst(), 2);
            								discFlist.set(FldCycleStartDetails.getInst(), 0);
            								discFlist.set(FldCycleEndDetails.getInst(), 2);
            								discFlist.set(FldPurchaseStartDetails.getInst(), 0);
            								discFlist.set(FldPurchaseEndDetails.getInst(), 2);
            							}
            							if(startDate != null && endDate != null) {
            								log.info("*** Term ***");
            								discFlist.set(FldUsageStartT.getInst(), startDate);
            								discFlist.set(FldCycleStartT.getInst(), startDate);
            								discFlist.set(FldPurchaseStartT.getInst(), startDate);
            								discFlist.set(FldPurchaseEndT.getInst(), endDate);
            								discFlist.set(FldUsageEndT.getInst(), endDate);
            								discFlist.set(FldCycleEndT.getInst(), endDate);
            								
            								discFlist.set(FldUsageStartDetails.getInst(), 0);
            								discFlist.set(FldUsageEndDetails.getInst(), 0);
            								discFlist.set(FldCycleStartDetails.getInst(), 0);
            								discFlist.set(FldCycleEndDetails.getInst(), 0);
            								discFlist.set(FldPurchaseStartDetails.getInst(), 0);
            								discFlist.set(FldPurchaseEndDetails.getInst(), 0);
            							
            							}
                							                							
                						}               						

                					
                				}
                				
                				
                				purchaseDeal(connector, accountPoid, dealDetailsFlist);
                			}
                		}
                		

                		
        			}    
        			log.info("Plan Change - Activation of new plan successful");
                    
                    connector.commitTransaction(accountPoid);
                    response.setResponseMsg("SUCCESS");
                    response.setResponseCode(ErrorCodes.SUCCESS);
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
                    log.info("Connector completes processing for SubscriptionDao - cancelDealWorker");
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


}

