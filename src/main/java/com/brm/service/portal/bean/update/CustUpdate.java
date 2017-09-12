package com.brm.service.portal.bean.update;

import java.util.ArrayList;
import java.util.List;


public class CustUpdate {
	private String accountNo;
	private PayInfo payInfo;
	private String action;
	private NameInfo nameInfo;
	private Billinfo billInfo;
	private List<NameInfo> nameInfoList = new ArrayList<NameInfo>();
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public PayInfo getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(PayInfo payInfo) {
		this.payInfo = payInfo;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public NameInfo getNameInfo() {
		return nameInfo;
	}
	public void setNameInfo(NameInfo nameInfo) {
		this.nameInfo = nameInfo;
	}
	public List<NameInfo> getNameInfoList() {
		return nameInfoList;
	}
	public void setNameInfoList(List<NameInfo> nameInfoList) {
		this.nameInfoList = nameInfoList;
	}
	public Billinfo getBillInfo() {
		return billInfo;
	}
	public void setBillInfo(Billinfo billInfo) {
		this.billInfo = billInfo;
	}
}
