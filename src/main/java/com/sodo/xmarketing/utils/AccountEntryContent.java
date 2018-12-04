/**
 * 
 */
package com.sodo.xmarketing.utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntryContent {

	// 	Thanh toán đơn hàng %s
		private String payOrder;

	  // Hoàn tiền hủy đơn hàng %s
	  private String refundCancelOrder;

	  // điện tử theo yêu cầu rút ví %s
	  private String rechargeWalletSubtractByRequest;
	  // nạp ví điện tử theo yêu cầu check thông tin nạp ví %s
	  private String rechargeWalletByRequestCheck;

	  // Trừ ví điện tử theo yêu cầu rút ví %s
	  private String withdrawWalletShipByRequest;

	  // Nạp ví theo yêu cầu check nạp tiền
	  private String depositRequest;
	  // Rút ví theo yêu cầu rút ví
	  private String withDrawalRequest;
	  // Nạp ví hoàn tiền khiếu nại
	  private String ticketRefund;
	  // Nạp ví trực tiếp từ tin nhắn
	  private String instantDepositMessage;
	  // Trừ quỹ theo yêu cầu rút ví
	  private String rechargeFundSubtractByRequest;
	  // Nạp quỹ theo yêu cầu nạp ví
	  private String rechargeWalletByRequest;
}
