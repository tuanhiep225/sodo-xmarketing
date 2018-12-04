/**
 * 
 */
package com.sodo.xmarketing.model.agency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sodo.xmarketing.model.agency.OrderModel.OrderModelBuilder;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderAgencyData {

	private static final Log LOGGER = LogFactory.getLog(OrderAgencyData.class);

	private String code;
	private ServicePricingAgency service;
	private BigDecimal price;
	private OrderStatus status;
	private Integer quantity;
	private String url;
	private Integer start;
	private Integer current;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Indexed(direction = IndexDirection.DESCENDING)
	private LocalDateTime createdDate;

}
