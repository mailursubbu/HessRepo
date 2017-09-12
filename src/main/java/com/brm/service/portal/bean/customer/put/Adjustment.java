package com.brm.service.portal.bean.customer.put;

public class Adjustment {
	
	private String accountNo;
	private double amount;
	private String mode;
	private String description;
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String descrption) {
		this.description = descrption;
	}
}
