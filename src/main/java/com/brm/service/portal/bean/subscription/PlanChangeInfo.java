package com.brm.service.portal.bean.subscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanChangeInfo {
	
	private String accountNo;
	private Date startDate;
	private Date endDate; 
	private List<ProdInfo> oldProdInfo = new ArrayList<ProdInfo>();
	private List<ProdInfo> newProdInfo = new ArrayList<ProdInfo>();
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public List<ProdInfo> getOldProdInfo() {
		return oldProdInfo;
	}
	public void setOldProdInfo(List<ProdInfo> prodInfo) {
		this.oldProdInfo = prodInfo;
	}	
	
	public List<ProdInfo> getNewProdInfo() {
		return newProdInfo;
	}
	public void setNewProdInfo(List<ProdInfo> prodInfo) {
		this.newProdInfo = prodInfo;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}		

}
