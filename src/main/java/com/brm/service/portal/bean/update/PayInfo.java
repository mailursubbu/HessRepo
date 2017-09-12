package com.brm.service.portal.bean.update;

public class PayInfo {
	private String action;
	private int paymentTerm = -1;
	private int paymentOffset;
	private int invType;
	private CcInfo ccInfo;
	private DdInfo ddInfo;
	private InvInfo invInfo;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getPaymentTerm() {
		return paymentTerm;
	}
	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	public int getPaymentOffset() {
		return paymentOffset;
	}
	public void setPaymentOffset(int paymentOffset) {
		this.paymentOffset = paymentOffset;
	}
	public int getInvType() {
		return invType;
	}
	public void setInvType(int invType) {
		this.invType = invType;
	}
	public CcInfo getCcInfo() {
		return ccInfo;
	}
	public void setCcInfo(CcInfo ccInfo) {
		this.ccInfo = ccInfo;
	}
	public InvInfo getInvInfo() {
		return invInfo;
	}
	public void setInvInfo(InvInfo invInfo) {
		this.invInfo = invInfo;
	}
	public DdInfo getDdInfo() {
		return ddInfo;
	}
	public void setDdInfo(DdInfo ddInfo) {
		this.ddInfo = ddInfo;
	}
}
