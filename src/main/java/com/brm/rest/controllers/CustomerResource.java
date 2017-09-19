package com.brm.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.CustomerDao;
import com.brm.rest.services.dao.iot.IotAccountDao;
import com.brm.rest.services.dao.iot.IotDeviceDao;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.customer.AccountInfo;
import com.brm.service.portal.bean.customer.CustomerInfo;
import com.brm.services.iot.model.InvalidRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

@RestController
@RequestMapping("/rest")
/*@Path("/accounts")*/
public class CustomerResource {
	@Autowired
	IotAccountDao iotAccDao;
	
	@Autowired
	IotDeviceDao iotDeviceDao;
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
	public Response createCustomer(@RequestBody AccountInfo account) {
		if(account.getPlanName().endsWith("IoT")){
			String iotAccId = iotAccDao.provisionIotAccount(account);
			String decodedIotAccId = decode(decode(iotAccId));
			iotDeviceDao.provisionIotDevice(account, decodedIotAccId);
		}
		
		CustomerDao cust = new CustomerDao();
		return cust.createCustomerAccount(account);	
	}
	
	//Error Handling    
		@ExceptionHandler(InvalidRequest.class)
		@ResponseStatus(HttpStatus.BAD_REQUEST)
		public Response invalidRequestHandler(InvalidRequest e) {
			return new Response(e.getMessage(),400);
		}
	
		public String decode(String s) {
		    return StringUtils.newStringUtf8(Base64.decodeBase64(s));
		}
		public String encode(String s) {
		    return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
		}	
}




