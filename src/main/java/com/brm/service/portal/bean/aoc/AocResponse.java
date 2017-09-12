package com.brm.service.portal.bean.aoc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AocResponse {

	String planName;
	private double totalCharge;
	private double totalDiscount;
	private double totalTax;
	private List<ChargeDetails> prodInfo = new ArrayList<ChargeDetails>();
	
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public double getTotalCharge() {
		return totalCharge;
	}
	public void setTotalCharge(double totalCharge) {
		this.totalCharge = totalCharge;
	}
	public double getTotalDiscount() {
		return totalDiscount;
	}
	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}
	public double getTotalTax() {
		return totalTax;
	}
	public void setTotalTax(double totalTax) {
		this.totalTax = totalTax;
	}
	public List<ChargeDetails> getProdInfo() {
		return prodInfo;
	}
	public void setProdInfo(List<ChargeDetails> prodInfo) {
		this.prodInfo = prodInfo;
	}
	public void addChargeDetails(ChargeDetails charge) {
		this.prodInfo.add(charge);
	}
}
