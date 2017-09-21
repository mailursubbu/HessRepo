package com.brm.rest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.service.portal.bean.Response;

//@Path("/test")
@RestController
@RequestMapping("/OBRMRESTService/rest/test")
public class TestService {
	
/*	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })*/
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public Response TestRestService() {
		Response resp = new Response();
		resp.setResponseCode(0);
		resp.setResponseMsg("Rest Service Working");
		return resp;
	}

}
