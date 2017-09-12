package com.brm.service.portal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import org.apache.log4j.Logger;
import com.brm.service.portal.bean.customer.AccountInfo;
import com.brm.service.portal.bean.customer.CustomerNameInfo;
import com.brm.service.portal.bean.customer.PhoneInfo;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.*;
import com.portal.pcm.fields.*;

public class FlistCreator {
	private long db = 1;
	private long id = -1;
	private static final Logger log = Logger.getLogger("Connector");
	
	public FList prepareChildFlist(AccountInfo accountInfo, FList childFlist) throws BRMException, EBufException {
		
		childFlist.remove(FldAccountObj.getInst());
		
		Date currentDate = new Date();
		log.info("start date --> " +accountInfo.getStartDate());
		long start_time = accountInfo.getStartDate().getTime() - currentDate.getTime();
		log.info("difference --> " +start_time);
		if (start_time < 0 && start_time > -60000)
			start_time = 0;
		long end_time = accountInfo.getEndDate().getTime() - currentDate.getTime();
		
		Date defaultDate = new Date(0);
		log.info("default date --> " +defaultDate);
		
		Date start_date = new Date(start_time);
		Date end_date = new Date(end_time);
		
		/*
		 * PIN_FLD_NAME_INFO
		*/
		List<CustomerNameInfo> nameInfoList  = accountInfo.getNameInfoList();
		List<PhoneInfo> phoneList  = null;
		int namesRecId = 1;
		int phoneRecId = 0;
		FList nameInfoFlist = null;
		FList phonesFlist = null;
		for (CustomerNameInfo nameInfo : nameInfoList) {
			log.info("adding nameinfo from the array");
			nameInfoFlist = new FList();
			nameInfoFlist.set(FldFirstName.getInst(), nameInfo.getFirstName());
			nameInfoFlist.set(FldLastName.getInst(), nameInfo.getLastName());		
			nameInfoFlist.set(FldSalutation.getInst(), nameInfo.getSalutation());
			nameInfoFlist.set(FldEmailAddr.getInst(), nameInfo.getEmailAddress());
			nameInfoFlist.set(FldCountry.getInst(), nameInfo.getCountry());
			nameInfoFlist.set(FldZip.getInst(), nameInfo.getZip());
			nameInfoFlist.set(FldState.getInst(), nameInfo.getState());
			nameInfoFlist.set(FldCity.getInst(), nameInfo.getCity());
			nameInfoFlist.set(FldAddress.getInst(), nameInfo.getAddress());
			phoneList = nameInfo.getPhones();
			phoneRecId = 0;
			for(PhoneInfo phone : phoneList) {
				phonesFlist = new FList();
				phonesFlist.set(FldPhone.getInst(), phone.getPhoneNo());
				phonesFlist.set(FldType.getInst(), phone.getType());
				nameInfoFlist.setElement(FldPhones.getInst(), phoneRecId++, phonesFlist);
			}
			childFlist.setElement(FldNameinfo.getInst(), namesRecId++, nameInfoFlist);
		}
		
		if(accountInfo.getNameInfo() != null) {
			log.info("adding nameinfo without the array");
			nameInfoFlist = new FList();
			nameInfoFlist.set(FldFirstName.getInst(), accountInfo.getNameInfo().getFirstName());
			nameInfoFlist.set(FldLastName.getInst(), accountInfo.getNameInfo().getLastName());		
			nameInfoFlist.set(FldSalutation.getInst(), accountInfo.getNameInfo().getSalutation());
			nameInfoFlist.set(FldEmailAddr.getInst(), accountInfo.getNameInfo().getEmailAddress());
			nameInfoFlist.set(FldCountry.getInst(), accountInfo.getNameInfo().getCountry());
			nameInfoFlist.set(FldZip.getInst(), accountInfo.getNameInfo().getZip());
			nameInfoFlist.set(FldState.getInst(), accountInfo.getNameInfo().getState());
			nameInfoFlist.set(FldCity.getInst(), accountInfo.getNameInfo().getCity());
			nameInfoFlist.set(FldAddress.getInst(), accountInfo.getNameInfo().getAddress());
		
			childFlist.setElement(FldNameinfo.getInst(), 1, nameInfoFlist);
		}
		
		/*
		 * PIN_FLD_ACCTINFO
		*/
		FList acctInfoFlist = new FList();
		acctInfoFlist.set(FldPoid.getInst(), new Poid(db, id, "/account"));
		acctInfoFlist.set(FldBusinessType.getInst(), 1);	// default to 1
		acctInfoFlist.setElement(FldBalInfo.getInst(), 0);
		acctInfoFlist.set(FldCurrency.getInst(), Integer.parseInt(accountInfo.getCurrency()));
		acctInfoFlist.set(FldAccountNo.getInst(), accountInfo.getAccountNo());
//		acctInfoFlist.set(FldCurrency.getInst(), PortalEnums.PinBillWhen.);
		childFlist.setElement(FldAcctinfo.getInst(), 0, acctInfoFlist);
		
		/*
		 * PIN_FLD_BALINFO
		*/
		FList balInfoFlist = new FList();
		balInfoFlist.set(FldPoid.getInst(), new Poid(db, id, "/balance_group"));		
		balInfoFlist.set(FldName.getInst(), "Balance Group<Account>");
		balInfoFlist.setElement(FldBillinfo.getInst(), 0);
		childFlist.setElement(FldBalInfo.getInst(), 0, balInfoFlist);
		
		/*
		 * PIN_FLD_BILLINFO
		*/
		FList billInfoFlist = new FList();
		billInfoFlist.set(FldPoid.getInst(), new Poid(db, id, "/billinfo"));		
		billInfoFlist.set(FldBillinfoId.getInst(), "Bill Unit(1)");
		billInfoFlist.set(FldPayType.getInst(), 10001);
		billInfoFlist.setElement(FldPayinfo.getInst(), 0);
		billInfoFlist.setElement(FldBalInfo.getInst(), 0);
		childFlist.setElement(FldBillinfo.getInst(), 0, billInfoFlist);
		
		/*
		 * PIN_FLD_LOCALES
		*/
		FList localeFlist = new FList();
		localeFlist.set(FldLocale.getInst(), "en_US");
		childFlist.setElement(FldLocales.getInst(), 0, localeFlist);
		
		/*
		 * PIN_FLD_STATUSES
		*/
		
		/*FList statusFlist = new FList();
		statusFlist.set(FldStatus.getInst(), PortalEnums.PinStatus.INACTIVE);
		statusFlist.set(FldStatusFlags.getInst(), PortalConst.STATUS_FLAG_ACTIVATE);
		childFlist.setElement(FldStatuses.getInst(), 0, statusFlist);*/
		
		/*
		 * PIN_FLD_PAYINFO
		*/
		FList payInfoFlist = new FList();
		payInfoFlist.set(FldName.getInst(), "Invoice1");
		payInfoFlist.set(FldPoid.getInst(), new Poid(db, id, "/payinfo"));
		payInfoFlist.set(FldPayType.getInst(), PortalEnums.PinBillType.INVOICE);
		
		FList invInfoFlist = new FList();
		invInfoFlist.set(FldDeliveryPrefer.getInst(), 0);
		invInfoFlist.set(FldInvTerms.getInst(), 0);
		invInfoFlist.set(FldName.getInst(), accountInfo.getPayInfo().getName());
		invInfoFlist.set(FldAddress.getInst(), accountInfo.getPayInfo().getAddress());
		invInfoFlist.set(FldCity.getInst(), accountInfo.getPayInfo().getCity());
		invInfoFlist.set(FldState.getInst(), accountInfo.getPayInfo().getState());
		invInfoFlist.set(FldZip.getInst(), accountInfo.getPayInfo().getZip());
		invInfoFlist.set(FldCountry.getInst(), accountInfo.getPayInfo().getCountry());
		invInfoFlist.set(FldEmailAddr.getInst(), accountInfo.getPayInfo().getEmailAddress());			
		invInfoFlist.set(FldDeliveryDescr.getInst(), accountInfo.getPayInfo().getEmailAddress());
		
		FList inhInfoFlist = new FList();
		inhInfoFlist.setElement(FldInvInfo.getInst(), 0, invInfoFlist);
		payInfoFlist.set(FldInheritedInfo.getInst(), inhInfoFlist);
		childFlist.setElement(FldPayinfo.getInst(), 0, payInfoFlist);
		/*
		 * PIN_FLD_SERVICES
		*/
		
		if (childFlist.hasField(FldServices.getInst())) {
			SparseArray serviceArray = childFlist.get(FldServices.getInst());
			Enumeration <?> servicesEnum = serviceArray.getValueEnumerator();
			
			while (servicesEnum.hasMoreElements()) {
				FList planSrvcFlist = (FList) servicesEnum.nextElement();
				String serviceType = planSrvcFlist.get(FldServiceObj.getInst()).getType();
				planSrvcFlist.set(FldLogin.getInst(), accountInfo.getLogin() + "-" + serviceType);
				planSrvcFlist.set(FldPasswdClear.getInst(), "password");
				
				//planSrvcFlist.set(FldDealObj.getInst(),null);
				
				//loop in through deals array
				if (planSrvcFlist.hasField(FldDeals.getInst())) {
					SparseArray dealsArray = planSrvcFlist.get(FldDeals.getInst());
					Enumeration <?> dealsEnum = dealsArray.getValueEnumerator();
					
					while (dealsEnum.hasMoreElements()) {
						FList planDealFlist = (FList) dealsEnum.nextElement();
						
						FList dealInfo = new FList();
						//if(!planSrvcFlist.hasField(FldDealInfo.getInst())){						
						dealInfo.set(FldName.getInst(), planDealFlist.get(FldName.getInst()));
						dealInfo.set(FldPoid.getInst(), planDealFlist.get(FldDealObj.getInst()));						
						dealInfo.set(FldStartT.getInst(), planDealFlist.get(FldStartT.getInst()));
						dealInfo.set(FldEndT.getInst(), planDealFlist.get(FldEndT.getInst()));
						dealInfo.set(FldFlags.getInst(), planDealFlist.get(FldFlags.getInst()));		
						dealInfo.set(FldDescr.getInst(), planDealFlist.get(FldDescr.getInst()));
						//}
						log.info("Deal Info --> "+dealInfo);
						
						//loop in through products array
						if (planDealFlist.hasField(FldProducts.getInst())) {
							SparseArray productsArray = planDealFlist.get(FldProducts.getInst());
							Enumeration <?> productsEnum = productsArray.getValueEnumerator();
							
							
						int i = 0;
							while (productsEnum.hasMoreElements()) {
								FList planProductFlist = (FList) productsEnum.nextElement();
								
								FList planProductFlist1 = new FList();
								log.info("1");
								planProductFlist1.set(FldQuantity.getInst(), planProductFlist.get(FldQuantity.getInst()));
								log.info("2");
								if (planProductFlist.get(FldOwnMax.getInst()) != null)
									
									planProductFlist1.set(FldOwnMax.getInst(), planProductFlist.get(FldOwnMax.getInst()));
									
								
								else
									
									planProductFlist1.set(FldOwnMax.getInst());
								
								log.info("3");
								if (planProductFlist.get(FldOwnMin.getInst()) != null)
									planProductFlist1.set(FldOwnMin.getInst(), planProductFlist.get(FldOwnMin.getInst()));
								else
									planProductFlist1.set(FldOwnMin.getInst());
								log.info("4");
								planProductFlist1.set(FldName.getInst(), planProductFlist.get(FldName.getInst()));
								log.info("5");
								planProductFlist1.set(FldProductObj.getInst(), planProductFlist.get(FldProductObj.getInst()));
								log.info("6");
								planProductFlist1.set(FldDescr.getInst(), planProductFlist.get(FldDescr.getInst()));
								log.info("7");
								planProductFlist1.set(FldUsageDiscount.getInst(), planProductFlist.get(FldUsageDiscount.getInst()));
								planProductFlist1.set(FldCycleDiscount.getInst(), planProductFlist.get(FldCycleDiscount.getInst()));
								planProductFlist1.set(FldPurchaseDiscount.getInst(), planProductFlist.get(FldPurchaseDiscount.getInst()));
								planProductFlist1.set(FldStatus.getInst(), planProductFlist.get(FldStatus.getInst()));
								planProductFlist1.set(FldStatusFlags.getInst(), planProductFlist.get(FldStatusFlags.getInst()));

								/*
								FList planProductFlist = (FList) productsEnum.nextElement();
								planProductFlist.set(FldPurchaseStartT.getInst(), accountInfo.getStartDate());
								planProductFlist.set(FldPurchaseEndT.getInst(), accountInfo.getEndDate());
								
								planProductFlist.set(FldPurchaseStartT.getInst(), accountInfo.getStartDate());
								planProductFlist.set(FldUsageStartT.getInst(), accountInfo.getStartDate());
								planProductFlist.set(FldCycleStartT.getInst(), accountInfo.getStartDate());	
								*/		
								
								
								//planProductFlist.set(FldPurchaseStartT.getInst(), accountInfo.getStartDate());
								//planProductFlist.set(FldUsageStartT.getInst(), accountInfo.getStartDate());
								//planProductFlist.set(FldCycleStartT.getInst(), accountInfo.getStartDate());
								
								SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY");
								Date date1=null;
								try {
									date1 = sdf.parse("01-01-1970");
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//if(((accountInfo.getEndDate() != null) && !(accountInfo.getEndDate().toString().isEmpty()) && !(accountInfo.getEndDate().toString().equalsIgnoreCase("null"))) == true) {								
								String startDate_temp = accountInfo.getEndDate().toString();
								log.info("*** Temp --> " + startDate_temp);
								log.info("*** Temp Date --> " + accountInfo.getEndDate());
								
								log.info("*** Todays Date --> " + currentDate);
								if(!accountInfo.getEndDate().before(currentDate)){
									log.info("*** Term 2 ***");
									planProductFlist1.set(FldPurchaseStartT.getInst(), start_date);
									planProductFlist1.set(FldUsageStartT.getInst(), start_date);
									planProductFlist1.set(FldCycleStartT.getInst(), start_date);
									
									
								//planProductFlist.set(FldPurchaseEndT.getInst(), accountInfo.getEndDate());						
								//planProductFlist.set(FldUsageEndT.getInst(), accountInfo.getEndDate());						
								//planProductFlist.set(FldCycleEndT.getInst(), accountInfo.getEndDate());
									
									planProductFlist1.set(FldPurchaseEndT.getInst(), end_date);						
									planProductFlist1.set(FldUsageEndT.getInst(), end_date);						
									planProductFlist1.set(FldCycleEndT.getInst(), end_date);
								
								planProductFlist1.set(FldUsageStartDetails.getInst(), 0);
								planProductFlist1.set(FldUsageEndDetails.getInst(), 0);
								planProductFlist1.set(FldCycleStartDetails.getInst(), 0);
								planProductFlist1.set(FldCycleEndDetails.getInst(), 0);
								planProductFlist1.set(FldPurchaseStartDetails.getInst(), 0);
								planProductFlist1.set(FldPurchaseEndDetails.getInst(), 0);
																
							}else{
								log.info("*** Perpetual 1 ***");
								log.info("*** START DATE --> "+start_date);
								planProductFlist1.set(FldPurchaseStartT.getInst(), start_date);
								planProductFlist1.set(FldUsageStartT.getInst(), start_date);
								planProductFlist1.set(FldCycleStartT.getInst(), start_date);
								
								planProductFlist1.set(FldPurchaseEndT.getInst(), defaultDate);						
								planProductFlist1.set(FldUsageEndT.getInst(), defaultDate);						
								planProductFlist1.set(FldCycleEndT.getInst(), defaultDate);
								
								
								planProductFlist1.set(FldUsageStartDetails.getInst(), 0);
								planProductFlist1.set(FldUsageEndDetails.getInst(), 2);
								planProductFlist1.set(FldCycleStartDetails.getInst(), 0);
								planProductFlist1.set(FldCycleEndDetails.getInst(), 2);
								planProductFlist1.set(FldPurchaseStartDetails.getInst(), 0);
								planProductFlist1.set(FldPurchaseEndDetails.getInst(), 2);								
							}
								dealInfo.setElement(FldProducts.getInst(), i, planProductFlist1);
								i=i+1;
						}
							
							log.info("Deal Info Last --> "+dealInfo);

					
					}
						planSrvcFlist.set(FldDealInfo.getInst(), dealInfo);
						log.info("planSrvcFlist --> "+planSrvcFlist);
				}
				
				//loop in through deal-info array
				if (planSrvcFlist.hasField(FldDealInfo.getInst())) {
					SparseArray productsArray = planSrvcFlist.get(FldDealInfo.getInst()).get(FldProducts.getInst());
					//loop in through products array
					Enumeration <?> productsEnum = productsArray.getValueEnumerator();
					while (productsEnum.hasMoreElements()) {
						FList planProductFlist = (FList) productsEnum.nextElement();
						//planProductFlist.set(FldPurchaseStartT.getInst(), accountInfo.getStartDate());
						//planProductFlist.set(FldUsageStartT.getInst(), accountInfo.getStartDate());
						//planProductFlist.set(FldCycleStartT.getInst(), accountInfo.getStartDate());
						
						SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY");
						Date date1=null;
						try {
							date1 = sdf.parse("01-01-1970");
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(!accountInfo.getEndDate().before(currentDate)) {
						/*	
						planProductFlist.set(FldPurchaseEndT.getInst(), accountInfo.getEndDate());						
						planProductFlist.set(FldUsageEndT.getInst(), accountInfo.getEndDate());						
						planProductFlist.set(FldCycleEndT.getInst(), accountInfo.getEndDate());
						*/
							log.info("*** Term ***");				
							planProductFlist.set(FldPurchaseStartT.getInst(), start_date);
							planProductFlist.set(FldUsageStartT.getInst(), start_date);
							planProductFlist.set(FldCycleStartT.getInst(), start_date);							
	
							planProductFlist.set(FldPurchaseEndT.getInst(), end_date);						
							planProductFlist.set(FldUsageEndT.getInst(), end_date);						
							planProductFlist.set(FldCycleEndT.getInst(), end_date);
						
						planProductFlist.set(FldUsageStartDetails.getInst(), 0);
						planProductFlist.set(FldUsageEndDetails.getInst(), 0);
						planProductFlist.set(FldCycleStartDetails.getInst(), 0);
						planProductFlist.set(FldCycleEndDetails.getInst(), 0);
						planProductFlist.set(FldPurchaseStartDetails.getInst(), 0);
						planProductFlist.set(FldPurchaseEndDetails.getInst(), 0);
						
						}
						else{
							planProductFlist.set(FldPurchaseStartT.getInst(), start_date);
							planProductFlist.set(FldUsageStartT.getInst(), start_date);
							planProductFlist.set(FldCycleStartT.getInst(), start_date);
							
							
							planProductFlist.set(FldUsageStartDetails.getInst(), 0);
							planProductFlist.set(FldUsageEndDetails.getInst(), 2);
							planProductFlist.set(FldCycleStartDetails.getInst(), 0);
							planProductFlist.set(FldCycleEndDetails.getInst(), 2);
							planProductFlist.set(FldPurchaseStartDetails.getInst(), 0);
							planProductFlist.set(FldPurchaseEndDetails.getInst(), 2);	
						}
					}
						
						
						

					
					
					
				}}
				Poid dealObj = new Poid(0);
				planSrvcFlist.set(FldDealObj.getInst(),dealObj);
			}
			
		}
		
		return childFlist;
		
	}
	
	public FList getPoidFromStrField(String objName, StrField fieldName, String fieldValue) {
		FList srchFlist = new FList();
		FList resultFlist = new FList();
		FList argsFlist = new FList();
		
		srchFlist.set(FldTemplate.getInst(),
				  " select X from " +objName+ " where F1 = V1 ");
		argsFlist.set(fieldName, fieldValue);
		srchFlist.setElement(FldArgs.getInst(), 1, argsFlist);
		srchFlist.setElement(FldResults.getInst(), 1, resultFlist); //set for result 
		
		return srchFlist;
	}
	
	public FList getPackageId(String objName, Poid accountPoid, Poid dealPoid, int status) {
		FList srchFlist = new FList();
		FList resultFlist = new FList();
		FList argsFlist = new FList();
		
		srchFlist.set(FldTemplate.getInst(),
				  " select X from " +objName+ " where F1 = V1 and F2 = V2 and F3 != V3 ");
		argsFlist.set(FldAccountObj.getInst(), accountPoid);
		srchFlist.setElement(FldArgs.getInst(), 1, argsFlist);
		argsFlist = new FList();
		argsFlist.set(FldDealObj.getInst(), dealPoid);
		srchFlist.setElement(FldArgs.getInst(), 2, argsFlist);
		argsFlist = new FList();
		argsFlist.set(FldStatus.getInst(), status);
		srchFlist.setElement(FldArgs.getInst(), 3, argsFlist);
		resultFlist.set(FldPackageId.getInst(), 0);
		srchFlist.setElement(FldResults.getInst(), 0, resultFlist); //set for result 
		
		return srchFlist;
	}

}
