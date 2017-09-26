package com.brm.services.iot.model;

import java.util.ArrayList;
import java.util.List;

import com.brm.service.portal.bean.customer.AccountInfo;
import com.brm.service.portal.bean.customer.CustomerNameInfo;
import com.brm.service.portal.bean.customer.PhoneInfo;
import com.portal.pcm.FList;

public final class IotAccCreate {
    public final CrmData crmData[];

    public IotAccCreate( CrmData[] crmData){
        this.crmData = crmData;
    }
    
    public IotAccCreate(AccountInfo accInfo){
    	ArrayList<CrmData> crmDataList = new ArrayList<CrmData>();
    	
    	CrmData crmData = new CrmData("id","");
    	crmDataList.add(crmData);
    	
    	//crmData = new CrmData("name",accInfo.getNameInfo().getFirstName() + " "+accInfo.getNameInfo().getLastName() );
    	crmData = new CrmData("name",getCustName(accInfo));
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("project_c","AxcessIOT");
    	crmDataList.add(crmData);
    	    	
    	crmData = new CrmData("email1",getCustEmail(accInfo));
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("password_c","123");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("re_password_c","123");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("first_record_c","1");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("master_monitoring_account_c","1");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("account_type_c","Monitoring Account");
    	crmDataList.add(crmData);
    	
    	this.crmData = crmDataList.toArray(new CrmData[crmDataList.size()]);
    }
    /*
     * [accountNo = SS000106, 
login = SS000106,
 startDate = Tue Sep 26 03: 00: 06 UTC 2017,
 endDate = Sun Mar 25 07: 00: 00 UTC 2018, 
 planName = Temperature and Humidity control online - IoT, 
 planPoid = null, 
 currency = 840, 
 active = true, 
 nameInfo = null, 
 payInfo = com.brm.service.portal.bean.customer.CustomerPayInfo @ 145b6ca7, 
 nameInfoList = [
 CustomerNameInfo[
 id = 1, address = 113, 
 country = US, state = CA, 
 zip = 95014, city = Cupertino, 
 emailAddress = amit.kumar @ synthesis - systems.com, 
 
 firstName = Test, 
 lastName = AKS1, salutation = Mr., phones = []], CustomerNameInfo[id = 1, address = 113, country = US, state = CA, zip = 95014, city = Cupertino, emailAddress = amit.kumar @ synthesis - systems.com, firstName = Test, lastName = AKS1, salutation = Mr., phones = []]], iotDeviceMac = null]

     */
    private String getCustName(AccountInfo accountInfo){
    	List<CustomerNameInfo> nameInfoList  = accountInfo.getNameInfoList();
		for (CustomerNameInfo nameInfo : nameInfoList) {
			if(nameInfo.getFirstName()!=null){
				return nameInfo.getFirstName() + nameInfo.getLastName();
			}
		}
		return "UnknowName";
    }
   private String getCustEmail(AccountInfo accountInfo){
    	List<CustomerNameInfo> nameInfoList  = accountInfo.getNameInfoList();
		for (CustomerNameInfo nameInfo : nameInfoList) {
			if(nameInfo.getEmailAddress()!=null){
				return nameInfo.getEmailAddress();
			}
		}
		return "UnknowName@gmail.com";
    }
   
   
}
