package com.brm.service.portal.bean.subscription;

public class ProdInfo {
	
	private String name;
	private boolean active = true;
	private PurchaseOverride purchaseOveride;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PurchaseOverride getPurchaseOveride() {
		return purchaseOveride;
	}
	public void setPurchaseOveride(PurchaseOverride purchaseOveride) {
		this.purchaseOveride = purchaseOveride;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

}
