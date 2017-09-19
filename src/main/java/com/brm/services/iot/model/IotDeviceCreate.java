package com.brm.services.iot.model;

import java.util.ArrayList;

import com.brm.service.portal.bean.customer.AccountInfo;

public final class IotDeviceCreate {
    CrmData crmData[];
    String  modulename;
    String accountId;
    String deviceType;

    public IotDeviceCreate( CrmData[] crmData){
        this.crmData = crmData;
    }
    
    public CrmData[] getCrmData() {
		return crmData;
	}


	public void setCrmData(CrmData[] crmData) {
		this.crmData = crmData;
	}


	public String getModulename() {
		return modulename;
	}


	public void setModulename(String modulename) {
		this.modulename = modulename;
	}


	public String getAccountId() {
		return accountId;
	}


	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}


	public String getDeviceType() {
		return deviceType;
	}


	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}


	public IotDeviceCreate(AccountInfo accInfo,String iotAccId){
        this.modulename="MHS01_Hosts";
        this.accountId=iotAccId;
        this.deviceType="sensor";
    	
    	ArrayList<CrmData> crmDataList = new ArrayList<CrmData>();
    	
    	CrmData crmData = new CrmData("id","");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("name",accInfo.getNameInfo().getFirstName() + " "+accInfo.getNameInfo().getLastName() );
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("alias_name_c",accInfo.getNameInfo().getFirstName() + " "+accInfo.getNameInfo().getLastName() );
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("unique_attribute_c",accInfo.getNameInfo().getFirstName() + " "+accInfo.getNameInfo().getLastName());
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("host_type_c","sensor");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("hub_c","0");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("device_endpoint__c","");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("resource_type_c","Device");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("latitude_c","");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("longitude_c","");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("hub_child_c","0");
    	crmDataList.add(crmData);
    	
    	this.crmData = crmDataList.toArray(new CrmData[crmDataList.size()]);
    	
    	accInfo.getAccountNo();
    }    
}
