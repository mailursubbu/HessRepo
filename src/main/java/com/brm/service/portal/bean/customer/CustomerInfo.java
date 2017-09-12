package com.brm.service.portal.bean.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author anup
 *
 */
@XmlRootElement
public class CustomerInfo {
	
	private String accountNo;
	private String status;
	private String accountPoidString;
    private String currency;    
    private Date effectiveT;
    private String errorMsg;
    private int errorCode;
    private CustomerNameInfo nameInfo;
    private BillInfo billInfo;
    private CustomerPayInfo payInfo;
    private List<CustomerNameInfo> nameInfoList = new ArrayList<CustomerNameInfo>();
    
	public CustomerInfo(String accountNo) {
		super();
		this.accountNo = accountNo;
	}
	
	public CustomerInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAccountPoidString() {
		return accountPoidString;
	}
	public void setAccountPoidString(String accountPoidString) {
		this.accountPoidString = accountPoidString;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getEffectiveT() {
		return effectiveT;
	}
	public void setEffectiveT(Date effectiveT) {
		this.effectiveT = effectiveT;
	}
	public CustomerNameInfo getNameInfo() {
		return nameInfo;
	}
	public void setNameInfo(CustomerNameInfo nameInfo) {
		this.nameInfo = nameInfo;
	}
	public BillInfo getBillInfo() {
		return billInfo;
	}
	public void setBillInfo(BillInfo billInfo) {
		this.billInfo = billInfo;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public CustomerPayInfo getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(CustomerPayInfo payInfo) {
		this.payInfo = payInfo;
	}
	public List<CustomerNameInfo> getNameInfoList() {
		return nameInfoList;
	}
	public void setNameInfoList(List<CustomerNameInfo> nameInfoList) {
		this.nameInfoList = nameInfoList;
	}
	public void addNameInfoList(CustomerNameInfo nameInfo) {
		this.nameInfoList.add(nameInfo);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	    
}
