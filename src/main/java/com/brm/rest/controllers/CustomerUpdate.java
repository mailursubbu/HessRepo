package com.brm.rest.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.CustUpdateDao;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.update.CustUpdate;

//@Path("/accounts")
@RestController
@RequestMapping("/OBRMRESTService/rest/accounts")
public class CustomerUpdate {
	
	/*@RolesAllowed("editor")
	@PUT
	@Path("{acctno}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/{acctno}", method = RequestMethod.PUT)
	@ResponseBody
	public Response UpdateCustomer(@PathVariable("acctno") String acctNo,@RequestBody CustUpdate custInfo) {
		CustUpdateDao updateDao = new CustUpdateDao();
		return updateDao.UpdateCustomer(acctNo, custInfo);
	}
	
	/*@RolesAllowed("editor")
	@PUT
	@Path("{acctno}/suspend")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/{acctno}/suspend", method = RequestMethod.PUT)
	@ResponseBody
	public Response SuspendCustomer(@PathVariable("acctno") String acctNo) {
		CustUpdateDao updateDao = new CustUpdateDao();
		return updateDao.SuspendAccount(acctNo);
		
	}
	
	/*@RolesAllowed("editor")
	@PUT
	@Path("{acctno}/unsuspend")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/{acctno}/unsuspend", method = RequestMethod.PUT)
	@ResponseBody
	public Response UnSuspendCustomer(@PathVariable("acctno") String acctNo) {
		CustUpdateDao updateDao = new CustUpdateDao();
		return updateDao.UnSuspendAccount(acctNo);
		
	}
	
	/*@RolesAllowed("editor")
	@PUT
	@Path("{acctno}/terminate")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/{acctno}/terminate", method = RequestMethod.PUT)
	@ResponseBody
	public Response CloseCustomer(@PathVariable("acctno") String acctNo) {
		CustUpdateDao updateDao = new CustUpdateDao();
		return updateDao.CloseAccount(acctNo);
		
	}

}
