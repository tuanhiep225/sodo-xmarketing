/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class ChargeModelDTO {
	
	@NotEmpty
	@NotNull
	private String content;
	@NotEmpty
	@NotNull
	private BigDecimal total;
	@NotEmpty
	@NotNull
	private String password;
}


