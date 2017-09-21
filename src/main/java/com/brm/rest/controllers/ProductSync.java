package com.brm.rest.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brm.rest.services.dao.ProductSyncDao;
import com.brm.service.portal.bean.sync.Catalogue;
import com.brm.service.portal.bean.sync.CatalogueList;
import com.brm.service.portal.bean.sync.Plan;


//@Path("/productcatalogue")
@RestController
@RequestMapping("/OBRMRESTService/rest/productcatalogue")
public class ProductSync {
	
	/*@RolesAllowed("member")
	@GET
	@Path("{name}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })*/
	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	@ResponseBody
	public List<Catalogue> getProductCatalogue(@PathVariable("name") String name) {
		
		ProductSyncDao sync = new ProductSyncDao();
		return sync.getProductCatalogue(name);
		
		/*List<Plan> planList = new ArrayList<Plan>();
		Plan plan = new Plan();
		plan.setName("Plan1");
		PlanServices planService;
		Products products = new Products();
		Deals deal = new Deals();
		deal.setName("D1");
		products.setName("D1P1");
	//	products.setAmount(10.00);
		products.setChargeType("One Time");
		products.setType("Product");
		deal.addProductList(products);
		
		products = new Products();
		products.setName("D1P2");
		//products.setAmount(20.00);
		products.setChargeType("Recurring");
		products.setType("Product");
		deal.addProductList(products);
		
		products = new Products();
		products.setName("D1d1");
		//products.setAmount();
		products.setChargeType("");
		products.setType("Discount");
		deal.addProductList(products);
		
		planService = new PlanServices();
		planService.setSrvcType("/service/telco/gsm/sms");
		planService.addDealList(deal);
		plan.addPlanServiceList(planService);
		
		deal = new Deals();
		deal.setName("D2");
		products = new Products();
		products.setName("D2P1");
		//products.setAmount(20.00);
		products.setChargeType("Recurring");
		products.setType("Product");
		deal.addProductList(products);
		
		planService = new PlanServices();
		planService.setSrvcType("/service/telco/gsm/telephony");
		planService.addDealList(deal);
		plan.addPlanServiceList(planService);
		planList.add(plan);
		
		deal = new Deals();
		deal.setName("D3");
		products = new Products();
		products.setName("D3P1");
		//products.setAmount(20.50);
		products.setChargeType("Recurring");
		products.setType("Product");
		deal.addProductList(products);
		
		planService = new PlanServices();
		planService.setSrvcType("/service/telco/gsm/gprs");
		planService.addDealList(deal);
		plan = new Plan();
		plan.addPlanServiceList(planService);
		planList.add(plan);
		
		List<Catalogue> catList = new ArrayList<Catalogue>();
		Catalogue cat = new Catalogue();
		cat.setProductName("Product 1");
		cat.setProductCode("xyz");
		cat.setProductDescr("Recurring charge $10.50/month");
		cat.setListPrice(10.50);
		cat.setPriceType("Recurring");
		cat.setStructureType("Add-On Product");
		cat.setRecurringCharge(true);
		catList.add(cat);
		
		cat = new Catalogue();
		cat.setProductName("Product 2");
		cat.setProductCode("xyz");
		cat.setProductDescr("Onetime charge $20");
		cat.setListPrice(20);
		cat.setPriceType("One Time");
		cat.setStructureType("Add-On Product");
		cat.setPlanName("Plan 99");
		catList.add(cat);
		
		cat = new Catalogue();
		cat.setProductName("Plan 99");
		cat.setProductCode("xyz");
		cat.setProductDescr("Plan charge $99/month");
//		cat.setListPrice(20);
//		cat.setPriceType("One Time");
		cat.setStructureType("Plan");
		catList.add(cat);
		
		return catList;
		*/
	}
	
	/*@RolesAllowed("member")
	@GET
	@Path("/plan/{name}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/plan/{name}", method = RequestMethod.GET)
	@ResponseBody
	public List<Plan> getPlanCatalogue(@PathVariable("name") String name) {
		
		ProductSyncDao sync = new ProductSyncDao();
		return sync.getProductCataloguePlan(name);
	
	}
	
	/*@RolesAllowed("member")
	@GET
	@Path("{name}/xml")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })*/
	@RequestMapping(value = "/{name}/xml", method = RequestMethod.GET)
	@ResponseBody
	public CatalogueList getPlanCatalogueList(@PathVariable("name") String name) {
		
		ProductSyncDao sync = new ProductSyncDao();
		return sync.getProductCatalogueList(name);
	}
	

}
