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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerChargingDTO {

	private static final Log LOGGER = LogFactory.getLog(CustomerChargingDTO.class);
	private BigDecimal balance;
	private String content;
}
