package com.brm.services.iot.model;


public class InvalidRequest extends RuntimeException {

	private static final long serialVersionUID = -7775286262238158148L;
	public InvalidRequest(String message) {
		super(message);
	}
	public InvalidRequest(String message,Throwable e) {
		super(message,e);
	}
}

