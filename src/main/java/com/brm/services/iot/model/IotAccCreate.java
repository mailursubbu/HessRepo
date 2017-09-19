package com.brm.services.iot.model;

import java.util.ArrayList;

import com.brm.service.portal.bean.customer.AccountInfo;

public final class IotAccCreate {
    public final CrmData crmData[];

    public IotAccCreate( CrmData[] crmData){
        this.crmData = crmData;
    }
    
    public IotAccCreate(AccountInfo accInfo){
    	ArrayList<CrmData> crmDataList = new ArrayList<CrmData>();
    	
    	CrmData crmData = new CrmData("id","");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("name",accInfo.getNameInfo().getFirstName() + " "+accInfo.getNameInfo().getLastName() );
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("project_c","AxcessIOT");
    	crmDataList.add(crmData);
    	
    	crmData = new CrmData("email1",accInfo.getNameInfo().getEmailAddress());
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
}
