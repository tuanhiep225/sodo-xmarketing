/**
 * 
 */
package com.sodo.xmarketing.model.agency;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.model.agency.OrderAgency.OrderAgencyBuilder;

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
public class ServicePricingAgency {

	private String code;
	private String name;
	private String description;
	private Integer miniumOrder;
	private Integer maxOrder;
	private BigDecimal price;
	private String unitName;
	private String speed;
	private Integer denominator;
	private Boolean isException;
	
}
