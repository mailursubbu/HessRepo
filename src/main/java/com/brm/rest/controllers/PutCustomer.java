package com.brm.rest.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.AdjustmentDao;
import com.brm.rest.services.dao.PaymentDao;
import com.brm.service.portal.bean.Response;
import com.brm.service.portal.bean.customer.put.Adjustment;
import com.brm.service.portal.bean.customer.put.Payment;

//@Path("/")
@RestController
@RequestMapping("/OBRMRESTService/rest/")
public class PutCustomer {
	
	/*@RolesAllowed("editor")
	@PUT
	@Path("payment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/payment", method = RequestMethod.PUT)
	@ResponseBody
	public Response postPayment(Payment payment) {
		PaymentDao payDao = new PaymentDao();
		return payDao.postPayment(payment);
	}
	
	/*@RolesAllowed("editor")
	@PUT
	@Path("adjustment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/adjustment", method = RequestMethod.PUT)
	@ResponseBody
	public Response postAdjustment(@RequestBody Adjustment adjustment) {
		
		AdjustmentDao adjDao = new AdjustmentDao();
		return adjDao.postAdjustment(adjustment);
	}


}
