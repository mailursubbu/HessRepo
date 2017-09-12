package com.brm.rest.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.CustomerDao;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.customer.AccountInfo;
import com.brm.service.portal.bean.customer.CustomerInfo;

@RestController
@RequestMapping("/rest")
/*@Path("/accounts")*/
public class CustomerResource {
	
	/*@RolesAllowed("member")
	@GET
	@Path("{acctno}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/accounts/{acctno}", method = RequestMethod.GET)
	@ResponseBody
	/*public CustomerInfo getCustomer(@PathParam("acctno") String acctNo,
					@HeaderParam("authorization") String authString) {*/
	public CustomerInfo getCustomer(@PathVariable("acctno") String acctNo,
			@RequestHeader("authorization") String authString) {
		
		CustomerDao cust = new CustomerDao();
		/*if(!isUserAuthenticated(authString)){
			return cust.getCustomerDetails(acctNo);
        }*/

		
		return cust.getCustomerDetails(acctNo);
	}
	
	/*@RolesAllowed("editor")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/accounts", method = RequestMethod.POST)
	@ResponseBody
	public Response createCustomer(AccountInfo account) {
		CustomerDao cust = new CustomerDao();
		return cust.createCustomerAccount(account);	
	}
	
	
}
