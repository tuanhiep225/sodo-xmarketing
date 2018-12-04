/**
 * 
 */
package com.sodo.xmarketing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sodo.xmarketing.dto.FundInfo;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.status.Distributor;
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
@AllArgsConstructor
@NoArgsConstructor
public class OrderExcel {
	String id;
	String url;
	String code;
	ServicePrice service;
	Integer start;
	Integer current;
	OrderStatus status;
	BigDecimal price;
	Integer quantity;
	String username; //
	LocalDateTime dateStart; // ngày bắt đầu
	LocalDateTime dateFinish; // ngày kết thúc
	BigDecimal refund; // số tiền hoàn trả
	String note; // nhân viên sẽ ghi chú đơn hàng vào đây
	String reason; // giải thích lý do tại sao hoàn trả hoặc cancel
	LocalDateTime timeReceive; // thời điểm mà nhân viên bắt đầu nhận đơn về xử lý;
	Integer dateCount; // số ngày khách hàng mong muốn sẽ hoàn thành xong;
	StaffDTO staff; // Nhân viên xử lý;
	Format format; // để cho nhân viên khi xử lý sẽ đúng kiểu tiền tệ của đơn hàng;
	Integer timeOrder; // sử dụng cho dịch vụ ngoại lệ
	Boolean isTrial = false;
	StaffDTO sale; // Xác định đơn hàng này của nhân viên sale nào
	@CreatedDate
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Indexed(direction = IndexDirection.DESCENDING)
	private LocalDateTime createdDate;

	@LastModifiedDate
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Indexed(direction = IndexDirection.DESCENDING)
	private LocalDateTime lastModifiedDate;

	private String createdBy;

	private String lastModifiedBy;

	OrderDistributor orderDistributor;
	
	String distributorCode;
	
	private BigDecimal priceNCC; // giá trị đơn hàng
	
	private Integer quantityNCC; // số lượng
	
	private String fundCode; // quỹ thực hiện
	
	private String fundName;
	
	public void convertBeforExport() {
		this.distributorCode = this.orderDistributor.getCode();
		this.priceNCC = this.orderDistributor.getPrice();
		this.quantityNCC = this.orderDistributor.getQuantity();
		this.fundCode = this.orderDistributor.getFundInfo() !=null ? this.orderDistributor.getFundInfo().getCode(): null;
		this.fundName = this.orderDistributor.getFundInfo() !=null ? this.orderDistributor.getFundInfo().getName(): null;
	}
}
