package com.cspire.prov.dtf.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ProvisioningFIPSCode")
public class IptvFipsCode{
	@Id
	private String fipsKey;
	private String state;
	private String county;
	private String f4;	
	private String f5;	
	private String f6;	
	private String f7;	
	private String f8;	
	private String f9;
	private String f10;
	public IptvFipsCode(){
		
	}
	@Override
	public String toString() {
		return "IptvFipsCode [fipsKey=" + fipsKey + ", state=" + state + ", county=" + county + ", f4=" + f4 + ", f5="
				+ f5 + ", f6=" + f6 + ", f7=" + f7 + ", f8=" + f8 + ", f9=" + f9 + ", f10=" + f10 + "]";
	}
	public IptvFipsCode(String fipsKey, String state, String county, String f4, String f5, String f6, String f7,
			String f8, String f9, String f10) {
		super();
		this.fipsKey = fipsKey;
		this.state = state;
		this.county = county;
		this.f4 = f4;
		this.f5 = f5;
		this.f6 = f6;
		this.f7 = f7;
		this.f8 = f8;
		this.f9 = f9;
		this.f10 = f10;
	}
	public String getFipsKey() {
		return fipsKey;
	}
	public void setFipsKey(String fipsKey) {
		this.fipsKey = fipsKey;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getF4() {
		return f4;
	}
	public void setF4(String f4) {
		this.f4 = f4;
	}
	public String getF5() {
		return f5;
	}
	public void setF5(String f5) {
		this.f5 = f5;
	}
	public String getF6() {
		return f6;
	}
	public void setF6(String f6) {
		this.f6 = f6;
	}
	public String getF7() {
		return f7;
	}
	public void setF7(String f7) {
		this.f7 = f7;
	}
	public String getF8() {
		return f8;
	}
	public void setF8(String f8) {
		this.f8 = f8;
	}
	public String getF9() {
		return f9;
	}
	public void setF9(String f9) {
		this.f9 = f9;
	}
	public String getF10() {
		return f10;
	}
	public void setF10(String f10) {
		this.f10 = f10;
	}	
	
}