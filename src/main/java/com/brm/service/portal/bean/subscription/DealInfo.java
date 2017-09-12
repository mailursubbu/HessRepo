package com.brm.service.portal.bean.subscription;

import java.util.ArrayList;
import java.util.List;

public class DealInfo {
	
	private String accountNo;
	private List<ProdInfo> prodInfo = new ArrayList<ProdInfo>();
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public List<ProdInfo> getProdInfo() {
		return prodInfo;
	}
	public void setProdInfo(List<ProdInfo> prodInfo) {
		this.prodInfo = prodInfo;
	}
}
