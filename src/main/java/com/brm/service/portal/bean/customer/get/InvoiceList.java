package com.brm.service.portal.bean.customer.get;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InvoiceList {
	private String accountPoid;
	private List<InvoiceInfo> invoices = new ArrayList<InvoiceInfo>();
	private boolean success = true;
	
	public String getAccountPoid() {
		return accountPoid;
	}
	public void setAccountPoid(String accountPoid) {
		this.accountPoid = accountPoid;
	}
	public List<InvoiceInfo> getInvoices() {
		return invoices;
	}
	public void setInvoices(List<InvoiceInfo> invoices) {
		this.invoices = invoices;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public void addInvoices(InvoiceInfo invInfo) {
		this.invoices.add(invInfo);
	}
}
