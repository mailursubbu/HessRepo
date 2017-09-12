package com.brm.service.portal.bean.sync;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Catalogue {
	
	private String name;
	//private String productCode;
	private String descr;
	private String serviceType;
//	private boolean recurringCharge = false;
	private String structureType = "None";
	private String status = "Active";
	private String priceType = "";
	private double listPrice;
	//private String planName = "None";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/*public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getSvcType() {
		return svcType;
	}
	public void setSvcType(String svcType) {
		this.svcType = svcType;
	}*/

	/*public boolean isRecurringCharge() {
		return recurringCharge;
	}
	public void setRecurringCharge(boolean recurringCharge) {
		this.recurringCharge = recurringCharge;
	}*/
	public String getStructureType() {
		return structureType;
	}
	public void setStructureType(String structureType) {
		this.structureType = structureType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	public double getListPrice() {
		return listPrice;
	}
	public void setListPrice(double listPrice) {
		this.listPrice = listPrice;
	}
	/*public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}*/
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
}
