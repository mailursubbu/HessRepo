package com.brm.services.iot.model;

public class CrmData {
    private  String name;
    private  String value;

    public CrmData( String name,String value){
        this.name = name;
        this.value = value;
    }

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}