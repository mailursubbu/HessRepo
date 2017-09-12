package com.brm.service.portal.bean.customer;

public class AddressInfo {
	
	private String city;
    private String country;
    private String postCode;
    private int phoneNo;

	public AddressInfo() {
		// TODO Auto-generated constructor stub
	}
	
	

	public AddressInfo(String city, String country, String postCode, int phoneNo) {
		super();
		this.city = city;
		this.country = country;
		this.postCode = postCode;
		this.phoneNo = phoneNo;
	}



	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public int getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(int phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	
}
