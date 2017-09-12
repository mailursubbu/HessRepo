package com.brm.service.portal.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement  
public class Response {
	
    private String responseMsg;
    private int responseCode;
    
        
    public Response() {
		super();
		this.responseCode = 101;
		this.responseMsg = "Generic Error";
		// TODO Auto-generated constructor stub
	}

	public Response(String responseMsg, int responseCode) {
		super();
		this.responseMsg = responseMsg;
		this.responseCode = responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

}
