package com.sodo.xmarketing.status;

import java.util.Arrays;
import java.util.List;

/**
 * @author HenryDo
 * @created 21/09/2017 11:28 AM
 */
public enum SystemDeterminant {
	// Trừ tiền ví (khách yêu cầu rút tiền)
	N1(1, "Nộp tiền nạp ví"), 
	
	WALLET_SUBTRACT(2, "Trừ ví theo yêu cầu rút tiền"),
	// Khác
	OTHER(3, "Khác"),
	// Nạp ví điện tử khách hàng theo yêu cầu
	WALLET_ADD_CUSTOMER(4, "Nạp ví điện tử theo yêu cầu check nạp ví"),

	ORDER_PAY(5, "Thanh toán đơn hàng"),

	// Nap vi hoan tien don hang
	ORDER_REFUND(6, "Hoàn tiền đơn hàng");

	private final int value;

	private final String reasonPhrase;

	private SystemDeterminant(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	public static List<Object> names() {
		List<Object> valuesStr = Arrays.asList(SystemDeterminant.values());
		return valuesStr;
	}

	public static void main(String[] arg0) {

		System.out.println(SystemDeterminant.names().get(0));
	}

	public int value() {
		return value;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
}
