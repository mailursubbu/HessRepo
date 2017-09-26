package com.brm.service.portal.bean.customer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.portal.pcm.Poid;

public class AccountInfo {
	private String accountNo;
	private String login;
	private Date startDate;
	private Date endDate;
	private String planName;
	private Poid planPoid;
	private String currency;
	private boolean active = true;
	private CustomerNameInfo nameInfo;
	private CustomerPayInfo payInfo;
	private List<CustomerNameInfo> nameInfoList = new ArrayList<CustomerNameInfo>();
	private String iotDeviceMac;
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public CustomerNameInfo getNameInfo() {
		return nameInfo;
	}
	public void setNameInfo(CustomerNameInfo nameInfo) {
		this.nameInfo = nameInfo;
	}
	public CustomerPayInfo getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(CustomerPayInfo payInfo) {
		this.payInfo = payInfo;
	}
	public Poid getPlanPoid() {
		return planPoid;
	}
	public void setPlanPoid(Poid planPoid) {
		this.planPoid = planPoid;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public List<CustomerNameInfo> getNameInfoList() {
		return nameInfoList;
	}
	public void setNameInfoList(List<CustomerNameInfo> nameInfoList) {
		this.nameInfoList = nameInfoList;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	
	public String getIotDeviceMac() {
		return iotDeviceMac;
	}
	public void setIotDeviceMac(String iotDeviceMac) {
		this.iotDeviceMac = iotDeviceMac;
	}
	public Date getEndDate() {
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Date tempEndDate = null;
		try{
		tempEndDate = df.parse("01-01-1970");
		}catch(ParseException e){
			e.printStackTrace();
		}
		if(endDate != null)			
			tempEndDate = endDate;
		
		return tempEndDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	@Override
	public String toString() {
		return "AccountInfo [accountNo=" + accountNo + ", login=" + login + ", startDate=" + startDate + ", endDate="
				+ endDate + ", planName=" + planName + ", planPoid=" + planPoid + ", currency=" + currency + ", active="
				+ active + ", nameInfo=" + nameInfo + ", payInfo=" + payInfo + ", nameInfoList=" + nameInfoList
				+ ", iotDeviceMac=" + iotDeviceMac + "]";
	}
	
}
