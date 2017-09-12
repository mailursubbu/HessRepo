package com.brm.service.portal.bean.customer.get;

import java.util.Date;

public class InvoiceInfo {
	
	private String billNo;
	private double dueAmount;
	private double billedAmount;
	private Date billDate;
	private Date dueDate;
	
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public double getDueAmount() {
		return dueAmount;
	}
	public void setDueAmount(double dueAmount) {
		this.dueAmount = dueAmount;
	}
	public double getBilledAmount() {
		return billedAmount;
	}
	public void setBilledAmount(double billedAmount) {
		this.billedAmount = billedAmount;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}
