package com.brm.service.portal.bean.subscription;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement 
public class SubscriptionInfo {
	
	private String accountNo;
	private List<ProductOffering> prodList = new ArrayList<ProductOffering>();
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public List<ProductOffering> getProdList() {
		return prodList;
	}
	public void setProdList(List<ProductOffering> prodList) {
		this.prodList = prodList;
	}
	public void addProdList(ProductOffering prodOffer) {
		this.prodList.add(prodOffer);
	}
	
}
