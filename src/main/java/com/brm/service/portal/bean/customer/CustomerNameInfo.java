package com.brm.service.portal.bean.customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerNameInfo {
	private int id = 1;
	private String address;
	private String country;
	private String state;
	private String zip;
	private String city;
	private String emailAddress;
	private String firstName;
	private String lastName;
	private String salutation;
	private List<PhoneInfo> phones = new ArrayList<PhoneInfo>();
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSalutation() {
		return salutation;
	}
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<PhoneInfo> getPhones() {
		return phones;
	}
	public void setPhones(List<PhoneInfo> phones) {
		this.phones = phones;
	}
	public void addPhoneInfoList(PhoneInfo phones) {
		this.phones.add(phones);
	}
	@Override
	public String toString() {
		return "CustomerNameInfo [id=" + id + ", address=" + address + ", country=" + country + ", state=" + state
				+ ", zip=" + zip + ", city=" + city + ", emailAddress=" + emailAddress + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", salutation=" + salutation + ", phones=" + phones + "]";
	}
	
}
