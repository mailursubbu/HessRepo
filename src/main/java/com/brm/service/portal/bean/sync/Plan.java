package com.brm.service.portal.bean.sync;

import java.util.ArrayList;
import java.util.List;

public class Plan {
	
	private String name;
	private String descr;
	private List<PlanServices> planServiceList = new ArrayList<PlanServices>();
	private int planType = 0;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public List<PlanServices> getPlanServiceList() {
		return planServiceList;
	}
	public void setPlanServiceList(List<PlanServices> planServiceList) {
		this.planServiceList = planServiceList;
	}
	public void addPlanServiceList(PlanServices planService) {
		this.planServiceList.add(planService);
	}
	public int getPlanType() {
		return planType;
	}
	public void setPlanType(int planType) {
		this.planType = planType;
	}
}
