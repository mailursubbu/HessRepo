package com.brm.rest.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.BalanceDao;
import com.brm.rest.services.dao.InvoiceDao;
import com.brm.rest.services.dao.PaymentDao;
import com.brm.service.portal.bean.customer.get.BalanceSummary;
import com.brm.service.portal.bean.customer.get.InvoiceList;
import com.brm.service.portal.bean.customer.get.PaymentInfo;
import com.brm.service.portal.bean.customer.get.PaymentList;

//@Path("/")
@RestController
@RequestMapping("/rest")
public class GetCutomerInfo {
	
/*	@RolesAllowed("member")
	@GET
	@Path("invoice/{acctno}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public  List<InvoiceInfo> getInvoiceHistory(@PathParam("acctno") String acctNo,
			@QueryParam("pageSize") @DefaultValue("6") int count) {
		
		InvoiceDao invDao = new InvoiceDao();
		return invDao.getInvoiceDetails(acctNo, count);
	}*/
	
	/*@RolesAllowed("member")
	@GET
	@Path("invoice/{acctno}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/invoice/{acctno}", method = RequestMethod.GET)
	@ResponseBody
	public  InvoiceList getInvoiceHistory(@PathVariable("acctno") String acctNo,
		/*	@QueryParam("pageSize") @DefaultValue("6") int count*/
			@RequestParam(value="pageSize", defaultValue="6") int count	) {
		InvoiceDao invDao = new InvoiceDao();
		return invDao.getInvoiceDetails(acctNo, count);
	}
	
	/*@RolesAllowed("member")
	@GET
	@Path("payment/{acctno}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/payment/{acctno}", method = RequestMethod.GET)
	@ResponseBody
	public  List<PaymentInfo> getPaymentHistory(@PathVariable("acctno") String acctNo,
			@RequestParam(value="pageSize", defaultValue="6") int count) {
		
		PaymentDao payDao = new PaymentDao();
		return payDao.getPaymentDetails(acctNo, count);
	}
	
	/*@RolesAllowed("member")
	@GET
	@Path("payment1/{acctno}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/payment1/{acctno}", method = RequestMethod.GET)
	@ResponseBody
	public  PaymentList getPaymentHistory1(@PathVariable("acctno") String acctNo,
			@RequestParam(value="pageSize", defaultValue="6") int count) {
		
		PaymentDao payDao = new PaymentDao();
		return payDao.getPaymentDetails1(acctNo, count);
	}
	
	/*@RolesAllowed("member")
	@GET
	@Path("balance/{acctno}/summary")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })*/
	@RequestMapping(value = "/balance/{acctno}/summary", method = RequestMethod.GET)
	@ResponseBody
	public BalanceSummary getBalanceSummary(@PathVariable("acctno") String acctNo) {
		BalanceDao balDao = new BalanceDao();
		return balDao.getBalance(acctNo);
	
	}
	
	/*@RolesAllowed("member")
	@GET
	@Path("balance/{acctno}/detail")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })*/
	@RequestMapping(value = "balance/{acctno}/detail", method = RequestMethod.GET)
	@ResponseBody
	public void getBalanceDetail(@PathVariable("acctno") String acctNo) {
	
	}
	

}
