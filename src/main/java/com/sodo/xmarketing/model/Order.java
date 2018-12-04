/**
 * 
 */
package com.sodo.xmarketing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.entity.BaseEntity;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@QueryEntity
@Document(collection = "order")
public class Order extends BaseEntity<String> {

	String id;
	String url;
	@Indexed
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
	Distributor distributor; // Nhà cung cấp dịch vụ
	Boolean isTrial = false;
	String from; // xác định đơn hàng tạo từ đâu;
	StaffDTO sale; // Xác định đơn hàng này của nhân viên sale nào
	
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ServicePrice getService() {
		return service;
	}

	public void setService(ServicePrice service) {
		this.service = service;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getCurrent() {
		return current;
	}

	public void setCurrent(Integer current) {
		this.current = current;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
}
