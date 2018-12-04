/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.model.OrderDistributor;

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
public class OrderDistributorSearch {
	
	private String code;
	
	private String orderCode;
	
	private String username; // username của khách hàng
	
	private String staffCode; // Mã nhân viên xử lý
		
	private String distributorCode; // Mã nhà cung cấp
	
	private String startDate;
	
	private String endDate;
	
	private BigDecimal fromValue;
	
	private BigDecimal toValue;
	
	private String status; // Trạng thái đơn nhà cung cấp
	
	private String serviceCode;
	
	private String transactionStatus; // Trạng thái thanh toán
	 
}
