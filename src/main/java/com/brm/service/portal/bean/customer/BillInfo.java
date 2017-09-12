package com.brm.service.portal.bean.customer;

import java.util.Date;

public class BillInfo {
	private int dom;
	private int billFrequency;
	private Date lastBillDate;
	private Date nextBillDate;
	
	public int getDom() {
		return dom;
	}
	public void setDom(int dom) {
		this.dom = dom;
	}
	public int getBillFrequency() {
		return billFrequency;
	}
	public void setBillFrequency(int billFrequency) {
		this.billFrequency = billFrequency;
	}
	public Date getLastBillDate() {
		return lastBillDate;
	}
	public void setLastBillDate(Date lastBillDate) {
		this.lastBillDate = lastBillDate;
	}
	public Date getNextBillDate() {
		return nextBillDate;
	}
	public void setNextBillDate(Date nextBillDate) {
		this.nextBillDate = nextBillDate;
	}
}
