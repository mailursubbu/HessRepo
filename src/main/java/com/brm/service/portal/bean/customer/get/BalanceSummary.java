package com.brm.service.portal.bean.customer.get;

import java.util.ArrayList;
import java.util.List;

public class BalanceSummary {
	private String accountNo;
	private List<SummaryBal> balanceList = new ArrayList<SummaryBal>();
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public List<SummaryBal> getBalanceList() {
		return balanceList;
	}
	public void setBalanceList(List<SummaryBal> balanceList) {
		this.balanceList = balanceList;
	}
	public void addBalList(SummaryBal balance) {
		this.balanceList.add(balance);
	}
}
