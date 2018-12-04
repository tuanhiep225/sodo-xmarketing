/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.math.BigDecimal;

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
public class ServicePricingDTO {

	private static final Log LOGGER = LogFactory.getLog(ServicePricingDTO.class);
	
	private String name;
	private String unitName;
	private BigDecimal wholesalePrices;
	private BigDecimal price;
	private Integer denominator;
	private Integer miniumOrder;
	private String speed;
	private String description;
	private String culture;
	private String code;
	private Integer maxOrder;
	private Integer minMinute;
	private Boolean allowTrial;
	private BigDecimal priceVip2;
	
	
}
