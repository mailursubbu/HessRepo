package com.brm.rest.services.dao;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import com.brm.service.portal.PCMWrapper;
import com.brm.service.portal.bean.sync.Balances;
import com.brm.service.portal.bean.sync.Catalogue;
import com.brm.service.portal.bean.sync.CatalogueList;
import com.brm.service.portal.bean.sync.Deals;
import com.brm.service.portal.bean.sync.Plan;
import com.brm.service.portal.bean.sync.PlanServices;
import com.brm.service.portal.bean.sync.Products;
import com.brm.service.portal.utils.BRMException;
import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.Poid;
import com.portal.pcm.PortalOp;
import com.portal.pcm.SparseArray;
import com.portal.pcm.fields.FldAccountObj;
import com.portal.pcm.fields.FldArgs;
import com.portal.pcm.fields.FldBalImpacts;
import com.portal.pcm.fields.FldDeals;
import com.portal.pcm.fields.FldDescr;
import com.portal.pcm.fields.FldDiscounts;
import com.portal.pcm.fields.FldElementId;
import com.portal.pcm.fields.FldEventType;
import com.portal.pcm.fields.FldFixedAmount;
import com.portal.pcm.fields.FldMembers;
import com.portal.pcm.fields.FldName;
import com.portal.pcm.fields.FldObject;
import com.portal.pcm.fields.FldParameters;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldProductObj;
import com.portal.pcm.fields.FldProducts;
import com.portal.pcm.fields.FldQuantityTiers;
import com.portal.pcm.fields.FldRatePlans;
import com.portal.pcm.fields.FldRates;
import com.portal.pcm.fields.FldResults;
import com.portal.pcm.fields.FldScaledAmount;
import com.portal.pcm.fields.FldServiceObj;
import com.portal.pcm.fields.FldServices;
import com.portal.pcm.fields.FldTemplate;
import com.portal.pcm.fields.FldTypeStr;


public class ProductSyncDao {
	
	private static final Logger log = Logger.getLogger("Connector");
	
	public CatalogueList getProductCatalogueList(String name) {
		CatalogueList catlList = new CatalogueList();
		catlList.setProducts(getProductCatalogue(name));
		return catlList;
	}
	
	public List<Plan> getProductCataloguePlan(String name) {
		return getPlanList(name);
	}
	
	public List<Catalogue> getProductCatalogue(String name) {
		List<Catalogue> catList = new ArrayList<Catalogue>();
		List<Plan> planList = getPlanList(name);
		Catalogue oneCatl;
		for (Plan onePlan : planList) {
			if(onePlan.getPlanType() == 0 || onePlan.getPlanType() == 2) {
				oneCatl = new Catalogue();
				oneCatl.setName(onePlan.getName());
				oneCatl.setDescr(onePlan.getDescr());
				oneCatl.setStructureType("Plan");
				if(onePlan.getPlanType() == 2)
					oneCatl.setStatus("In-active");
				catList.add(oneCatl);
				continue;
			}
			for(PlanServices plService : onePlan.getPlanServiceList()) {
				for(Deals oneDeal : plService.getDealList()) {
					for(Products oneProduct : oneDeal.getProductList()) {
						log.info("in product for plan:" +onePlan.getName());
						oneCatl = new Catalogue();
						catList.add(oneCatl);
						oneCatl.setName(oneProduct.getName());
						oneCatl.setDescr(oneProduct.getDescr());
						oneCatl.setStructureType(oneProduct.getType());
						oneCatl.setServiceType(plService.getSrvcType());
						if(oneProduct.getChargeType() != null ) {
							if(oneProduct.getChargeType().contains("cycle")) {
								oneCatl.setPriceType("Recurring");
							} else
								oneCatl.setPriceType("One Time");
						}
						if(onePlan.getPlanType() == 3)
							oneCatl.setStatus("In-active");
						for(Balances oneBal : oneProduct.getBalList()) {
							if(oneBal.getResourceId().intValue() <= 1000) {
								oneCatl.setListPrice(oneBal.getAmount());
								break;
							}
						}
					}
				}				
			}		
		}
		
		return catList;
	}
	
	public List<Plan> getPlanList(String name) {
		
		List<Plan> planList = new ArrayList<Plan>();
		PCMWrapper connector = new PCMWrapper();
		try {
			connector.createContext();
			searchPlanList(connector, name, 0, planList);
			searchPlanList(connector, name, 1, planList);
			searchPlanList(connector, name+" INACTIVE", 2, planList);
			searchPlanList(connector, name+" INACTIVE", 3, planList);
			
		} catch (BRMException ex) {
			log.error(ex.getMessage() + " { ERR_CODE: " + ex.getErrorCode() + " }");
			log.error(ex.getHiddenException());
			log.error(BRMException.getStackTraceAsString(ex));
		} catch (Exception ex) {
			log.error(BRMException.getStackTraceAsString(ex));
		} finally {
			log.info("Connector completes processing for getPlanList");
			try {				
	    		connector.closeContext();
	    	}catch (BRMException ex1) {
	    		log.fatal( "Error while closing connection. Error CODE: " 
	    				+ ex1.getErrorCode() + " Message: " + ex1.getMessage());
	    		log.fatal(BRMException.getStackTraceAsString(ex1));	
	    	}
		}
		
		
		return planList;

	}
	
	/************************************************************
	 * type => 0 = new
	 *         1 = addon
	 *         2 = INACTIVE new
	 *         3 = INACTIVE addon
	 ************************************************************
	*/
	
	private void searchPlanList(PCMWrapper connector, String name, int type, List<Plan> planList) throws EBufException, BRMException{

		FList readFlist = new FList();
		String typeStr = "new";
		if(type == 1 || type == 3)
			typeStr = "addon";
		FList retFlist = prepareInputFlist(name, typeStr);
		FList outFlist = connector.searchNoException(retFlist, 256, "ProductSyncDao: Search Plan");
		if (outFlist != null && outFlist.containsKey(FldResults.getInst())) {
			FList resultFlist = outFlist.getElement(FldResults.getInst(), 0);
			log.info("the result flist is:" +resultFlist);
			
			SparseArray memberArray = resultFlist.get(FldMembers.getInst());
			Enumeration <?> members = memberArray.getValueEnumerator();
			while (members.hasMoreElements()) {
				FList memberFlist = (FList)members.nextElement();
				readFlist.set(FldPoid.getInst(), memberFlist.get(FldObject.getInst()));
				FList planFlist = connector.callOpcode(PortalOp.CUST_POL_READ_PLAN, 
										readFlist, "ProductSyncDao:Read Plan");
//				log.info("the search plan outflist is: " +planFlist);
				planList.add(preparePlanList(connector, planFlist, type));
			}
		}	
		

	}
	
	private Plan preparePlanList(PCMWrapper connector, FList planFList, int planType) throws EBufException, BRMException{
		
		Plan onePlan = new Plan();
		onePlan.setName(planFList.get(FldName.getInst()));
		onePlan.setDescr(planFList.get(FldDescr.getInst()));
		onePlan.setPlanType(planType);
		PlanServices plService;
		Deals oneDeal;
		Products oneProduct;
		Balances oneBalance;
		FList prodInfoFlist = new FList();
		SparseArray serviceArray = planFList.get(FldServices.getInst());
		Enumeration <?> services = serviceArray.getValueEnumerator();
		while (services.hasMoreElements()) {
			FList servicesFlist = (FList)services.nextElement();
			plService = new PlanServices();
			plService.setSrvcType(servicesFlist.get(FldServiceObj.getInst()).getType());
			onePlan.addPlanServiceList(plService);
			
			SparseArray dealsArray = servicesFlist.get(FldDeals.getInst());
			Enumeration <?> deals = dealsArray.getValueEnumerator();
			while (deals.hasMoreElements()) {
				FList dealsFlist = (FList)deals.nextElement();
				oneDeal = new Deals();
				if(dealsFlist.containsKey(FldName.getInst()))
					oneDeal.setName(dealsFlist.get(FldName.getInst()));
				plService.addDealList(oneDeal);
				if(dealsFlist.containsKey(FldProducts.getInst())) {			
					SparseArray productsArray = dealsFlist.get(FldProducts.getInst());
					Enumeration <?> products = productsArray.getValueEnumerator();
					while (products.hasMoreElements()) {
						FList productsFlist = (FList)products.nextElement();
						oneProduct = new Products();
						oneProduct.setName(productsFlist.get(FldName.getInst()));
						oneProduct.setDescr(productsFlist.get(FldDescr.getInst()));
						oneDeal.addProductList(oneProduct);
						
						prodInfoFlist.set(FldPoid.getInst(), productsFlist.get(FldProductObj.getInst()));
					//	log.info("the product poid is:" +prodInfoFlist);
						FList getProdFlist = connector.callOpcode(PortalOp.PRICE_GET_PRODUCT_INFO, 1,
											prodInfoFlist, "getPlanList");
					//	log.info("the output is: " +getProdFlist);
						FList productFlist = getProdFlist.getElement(FldProducts.getInst(), 0);
						FList ratePlanFlist = productFlist.getElement(FldRatePlans.getInst(), 0);
						oneProduct.setChargeType(ratePlanFlist.get(FldEventType.getInst()));
						oneProduct.setType("Add-on Product");
						FList ratesFlist = ratePlanFlist.getElement(FldRates.getInst(), 0);
						FList qtyTiersFlist = ratesFlist.getElement(FldQuantityTiers.getInst(), 0);
						if (qtyTiersFlist.containsKey(FldBalImpacts.getInst())) {
							SparseArray balanceArray = qtyTiersFlist.get(FldBalImpacts.getInst());
							Enumeration <?> balances = balanceArray.getValueEnumerator();
							while(balances.hasMoreElements()) {
								FList balImpFlist = (FList)balances.nextElement();
								oneBalance = new Balances();
								oneBalance.setResourceId(balImpFlist.get(FldElementId.getInst()));
								double fixedAmt = balImpFlist.get(FldFixedAmount.getInst()).doubleValue();
								double scaledAmt = balImpFlist.get(FldScaledAmount.getInst()).doubleValue();
								oneBalance.setAmount(fixedAmt + scaledAmt);
								oneProduct.addBalList(oneBalance);
							}
						}
						
					}
				}
				if(dealsFlist.containsKey(FldDiscounts.getInst())) {
					SparseArray discountsArray = dealsFlist.get(FldDiscounts.getInst());
					Enumeration <?> discounts = discountsArray.getValueEnumerator();
					while (discounts.hasMoreElements()) {
						FList discountsFlist = (FList)discounts.nextElement();
						oneProduct = new Products();
						oneProduct.setName(discountsFlist.get(FldName.getInst()));
						oneProduct.setDescr(discountsFlist.get(FldDescr.getInst()));
						oneDeal.addProductList(oneProduct);
						oneProduct.setType("Add-on Discount");
						
					}
				}
			}
		}

		
		return onePlan;
		
	}
	
	private FList prepareInputFlist(String name, String typeStr) {
		FList inputFlist = new FList();
		inputFlist.set(FldTemplate.getInst(), 
				"select X from /group/$1 where F1 = V1 and F2 = V2 and F3 = V3 and F4 = V4 ");
		inputFlist.set(FldParameters.getInst(), "plan_list");
		FList argsFlist = new FList();
		argsFlist.set(FldName.getInst(), name);
		inputFlist.setElement(FldArgs.getInst(), 1, argsFlist);
		argsFlist = new FList();
		argsFlist.set(FldTypeStr.getInst(), typeStr);
		inputFlist.setElement(FldArgs.getInst(), 2, argsFlist);
		argsFlist = new FList();
		argsFlist.set(FldPoid.getInst(), new Poid(1, -1, "/group/plan_list"));
		inputFlist.setElement(FldArgs.getInst(), 3, argsFlist);
		argsFlist = new FList();
		argsFlist.set(FldAccountObj.getInst(), new Poid(1, -1, "/account"));
		inputFlist.setElement(FldArgs.getInst(), 4, argsFlist);
		FList resultFlist = new FList();
		resultFlist.setElement(FldMembers.getInst(), -1);
		inputFlist.setElement(FldResults.getInst(), 0, resultFlist);
		log.info("the search flist is:" +inputFlist);
		
		return inputFlist;
	}

}
