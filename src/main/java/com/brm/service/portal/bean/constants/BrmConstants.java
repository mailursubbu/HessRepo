package com.brm.service.portal.bean.constants;

public class BrmConstants {
	
	public enum PayType {
		CREDIT_CARD(10003),
		DEBIT_CARD(10005),
		INVOICE(10001),
		DD(10005),
		SUBORD(10007),
		CASH(10011),
		CHEQUE(10012),
		WIRE_TRANSFER(10013),
		PAY_ORDER(10014),
		POSTAL_ORDER(10015)
		;
		private int value;
		
		private PayType(int value) {
			this.value = value;
		}
		
		public int value() {
			return this.value;
		}
		
	}
	
}
