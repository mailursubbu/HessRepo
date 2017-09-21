package com.brm.rest.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.AdviceOfChargeDao;
import com.brm.service.portal.bean.aoc.AocRequest;
import com.brm.service.portal.bean.aoc.AocResponse;

/*@Path("/aoc")*/
@RestController
@RequestMapping("/OBRMRESTService/rest/")
public class AdviceOfCharge {
	
	/*@RolesAllowed("member")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/aoc", method = RequestMethod.POST)
	@ResponseBody
	public AocResponse getAOC(@RequestBody AocRequest request) {
		AdviceOfChargeDao aocDao = new AdviceOfChargeDao();
		return aocDao.getAdviceOfCharge(request);
	}
	

}
