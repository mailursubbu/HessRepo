package com.brm.rest.controllers;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.SubscriptionDao;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.subscription.DealInfo;
import com.brm.service.portal.bean.subscription.PlanChangeInfo;
import com.brm.service.portal.bean.subscription.ProductOffering;
import com.brm.service.portal.bean.subscription.SubscriptionInfo;

//@Path("/subscriptions")
@RestController
@RequestMapping("/OBRMRESTService/rest/subscriptions")
public class SubscriptionResource {
	
	private static final Logger log = Logger.getLogger("Connector");
	
	/*@RolesAllowed("member")
	@GET
	@Path("{acctno}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/{acctno}", method = RequestMethod.GET)
	@ResponseBody
	public SubscriptionInfo getSubscription(@PathVariable("acctno") String acctNo) {
		
		SubscriptionInfo subscInfo = new SubscriptionInfo();
		subscInfo.setAccountNo(acctNo);
		ProductOffering prodOffer = new ProductOffering();
		prodOffer.setName("Product 1");
		prodOffer.setCharge(10);
		prodOffer.setChargeType("NRC");
		prodOffer.setQuantity(1);
		prodOffer.setStartTime("01-01-2016");
		subscInfo.addProdList(prodOffer);
		
		prodOffer = new ProductOffering();
		prodOffer.setName("Product 2");
		prodOffer.setCharge(20);
		prodOffer.setChargeType("MRC");
		prodOffer.setQuantity(1);
		prodOffer.setStartTime("01-01-2016");
		subscInfo.addProdList(prodOffer);
		
		prodOffer = new ProductOffering();
		prodOffer.setName("Product 3");
		//prodOffer.setCharge(20);
		prodOffer.setChargeType("USG");
		prodOffer.setQuantity(1);
		subscInfo.addProdList(prodOffer);
		
		return subscInfo;
		
	}
	
	/*@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })*/
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public Response purchaseDeal(@RequestBody DealInfo dInfo) {
		log.info("Entered purchaseDeal method of SubscriptionResource class");
		SubscriptionDao subscr = new SubscriptionDao();
		return subscr.purchaseDealWorker(dInfo);
	}
	
	/*@POST
    @Path("cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })*/
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	@ResponseBody
    public Response cancelDeal(@RequestBody DealInfo dInfo) {
         
          SubscriptionDao subscr = new SubscriptionDao();
          return subscr.cancelDealWorker(dInfo);
    }
	
	/*@POST
    @Path("planchange")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })*/
	@RequestMapping(value = "/planchange", method = RequestMethod.POST)
	@ResponseBody
    public Response changeDeal(@RequestBody PlanChangeInfo dInfo) {
         
          SubscriptionDao subscr = new SubscriptionDao();
          return subscr.changeDealWorker(dInfo);
    }	

}
