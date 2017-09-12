package com.brm.service.portal.bean.customer.get;

import java.util.ArrayList;
import java.util.List;

public class PaymentList {
	private List<PaymentInfo> payments = new ArrayList<PaymentInfo>();
	private boolean success = true;
	
	public List<PaymentInfo> getPayments() {
		return payments;
	}
	public void setPayments(List<PaymentInfo> payments) {
		this.payments = payments;
	}
	public void addPayments(PaymentInfo paymentInfo) {
		this.payments.add(paymentInfo);
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
