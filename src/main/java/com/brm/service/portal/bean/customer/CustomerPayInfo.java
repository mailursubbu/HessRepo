package com.brm.service.portal.bean.customer;

public class CustomerPayInfo {
	private String name;
	private String country;
	private String state;
	private String zip;
	private String city;
	private String emailAddress;
	private String address;
	private String payType;
	private String cardNo;
	private String cardExp;
	private String BankNo;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCardExp() {
		return cardExp;
	}
	public void setCardExp(String cardExp) {
		this.cardExp = cardExp;
	}
	public String getBankNo() {
		return BankNo;
	}
	public void setBankNo(String bankNo) {
		BankNo = bankNo;
	}
	@Override
	public String toString() {
		return "CustomerPayInfo [name=" + name + ", country=" + country + ", state=" + state + ", zip=" + zip
				+ ", city=" + city + ", emailAddress=" + emailAddress + ", address=" + address + ", payType=" + payType
				+ ", cardNo=" + cardNo + ", cardExp=" + cardExp + ", BankNo=" + BankNo + "]";
	}
	

}
