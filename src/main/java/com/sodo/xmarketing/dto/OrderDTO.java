/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
@Builder
public class OrderDTO {

	private Integer start;
	private Integer current;
	private String note;
	private BigDecimal refund;
	private String reason;
	private OrderStatus status;
	private Distributor distributor;
}
