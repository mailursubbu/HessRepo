package com.brm.service.portal.bean.aoc;

import java.util.ArrayList;
import java.util.List;

public class AocRequest {

	String planName;
	private List<Products> prodInfo = new ArrayList<Products>();
	
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public List<Products> getProdInfo() {
		return prodInfo;
	}
	public void setProdInfo(List<Products> prodInfo) {
		this.prodInfo = prodInfo;
	}
}
