/**
 * 
 */
package com.sodo.xmarketing.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sodo.xmarketing.dto.FundInfo;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.entity.BaseEntity;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.status.Distributor;
import com.sodo.xmarketing.status.OrderDistributorStatus;
import com.sodo.xmarketing.status.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "order-distributor")
public class OrderDistributor extends BaseEntity<String>{

	@Id
	private String id;
	
	private String code;

	private String orderCode;

	private String note;

	private Format format; 
	
	private FundInfo fundInfo; // quỹ thực hiện

	private Distributor distributor; // Nhà cung cấp

	private StaffDTO staff; // Nhân viên xử lý;

	private String username; // Xác định đơn nhà cung cấp này cho khách hàng nào

	private BigDecimal refund; // số tiền hoàn trả
	
	private String refundReason; // Lý do hoàn trả

	private ServicePrice service; // dịch vụ

	private OrderDistributorStatus status; // trạng thái đơn hàng
	
	private BigDecimal price; // giá trị đơn hàng
	
	private Integer quantity; // số lượng
	
	private TransactionStatus transactionStatus; // Trạng thái thanh toán
	
	private Boolean isRequestPayment; // true nếu đơn đã gửi yêu cầu thanh toán


}
