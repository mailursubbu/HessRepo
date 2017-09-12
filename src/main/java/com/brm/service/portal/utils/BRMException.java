package com.brm.service.portal.utils;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.portal.pcm.EBufException;

public class BRMException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Exception hiddenException;
	private boolean custom; //custom exception
	private int errorCode; //custom error codes
	private String pcpErrorCode; //PCP error code
	
	public BRMException(String error, int errorCode,  Exception excp) {
	    super(error);
	    hiddenException = excp;
	    this.errorCode = errorCode;
	    this.pcpErrorCode = "";
	    this.pcpErrorCode = ((EBufException) excp).getErrorString();
	    custom = false;
	  }
  
	public int getErrorCode(){
	    return errorCode;
	  }

	  public void setErrorCode(int errorCode){
	    this.errorCode = errorCode;
	  }

	  public void setEventId(String eventId) {
	    this.eventId = eventId;
	  }

	  public void setPcpErrorCode(String pcpErrorCode) {
	    this.pcpErrorCode = pcpErrorCode;
	  }

	  private String eventId;

	  public BRMException(String error, Exception excp) {
	    super(error);
	    hiddenException = excp;
	    custom = false;
	  }

	  public BRMException(String error) {
	    super(error);//custom exception and no root Exception
	    custom = true;
	  }
	  public BRMException(String error, int errorCode) {
	    super(error);//custom exception and no root Exception
	    this.errorCode = errorCode;
	    custom = true;
	  }

	  public BRMException(String error, int errorCode, String eventId) {
	    super(error);//custom exception and no root Exception
	    this.errorCode = errorCode;
	    this.eventId = eventId;
	    custom = true;
	  }

	  public static String getStackTraceAsString(Exception exception) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    pw.print(" [ ");
	    pw.print(exception.getClass().getName());
	    pw.print(" ] ");
	    pw.print(exception.getMessage());
	    exception.printStackTrace(pw);
//	    String stackTrace = sw.toString();

	    return sw.toString();
	  }

	  public boolean isCustomException() {
	    return custom;
	  }

	  public Exception getHiddenException() {
	    return (hiddenException);
	  }

	  public String getEventId() {
	    return eventId;
	  }

	  public String getPcpErrorCode() {
	    return pcpErrorCode;
	  }

	  void printRawStackTrace(){
	    if(!custom)
	      this.hiddenException.printStackTrace();
	  }
}
