package com.brm.service.portal.bean.sync;

import java.util.ArrayList;
import java.util.List;

public class Products {
	private String name;
	private String chargeType;
	private String type;
	private String descr;
	private List<Balances> balList = new ArrayList<Balances>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChargeType() {
		return chargeType;
	}
	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public List<Balances> getBalList() {
		return balList;
	}
	public void setBalList(List<Balances> balList) {
		this.balList = balList;
	}
	public void addBalList(Balances balance) {
		this.balList.add(balance);
	}
	
}
