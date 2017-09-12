package com.brm.rest.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.service.portal.bean.payment.PaymentURL;



//@Path("/payment")
@RestController
@RequestMapping("/rest")
public class PaymentService {
	
	/*@RolesAllowed("member")
	@GET
	@Path("{acctno}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	
	/*
	 * Error creating bean with name 'requestMappingHandlerMapping' defined in class path resource [org/springframework/boot/autoconfigure/web/WebMvcAutoConfiguration$EnableWebMvcConfiguration.class]: Invocation of init method failed; nested exception is java.lang.IllegalStateException: Ambiguous mapping. Cannot map 'paymentService' method 
public com.brm.service.portal.bean.payment.PaymentURL com.brm.rest.controllers.PaymentService.getURL(java.lang.String,java.lang.String)
to {[/rest/payment/{acctno}],methods=[GET]}: There is already 'getCutomerInfo' bean method
public java.util.List<com.brm.service.portal.bean.customer.get.PaymentInfo> com.brm.rest.controllers.GetCutomerInfo.getPaymentHistory(java.lang.String,int) mapped.
	 */
	/*@RequestMapping(value = "/payment/{acctno}", method = RequestMethod.GET)
	@ResponseBody
	public PaymentURL getURL(@PathVariable("acctno") String acctNo,
			@RequestHeader("authorization") String authString) {
		
		PaymentURL url = new PaymentURL();
		if (acctNo.equalsIgnoreCase("xxxxx")){
			url.setStatus("fail");		
		}
		else{
			url.setURL("https://www.google.co.in/");
			url.setStatus("success");
		}
		return url;
	}*/

}
