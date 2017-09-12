package com.brm.service.portal.bean.sync;

import java.util.ArrayList;
import java.util.List;

public class PlanServices {
	
	private String srvcType;
	private List<Deals> dealList = new ArrayList<Deals>();
	
	public String getSrvcType() {
		return srvcType;
	}
	public void setSrvcType(String srvcType) {
		this.srvcType = srvcType;
	}
	public List<Deals> getDealList() {
		return dealList;
	}
	public void setDealList(List<Deals> dealList) {
		this.dealList = dealList;
	}
	
	public void addDealList(Deals deals) {
		this.dealList.add(deals);
	}

}
