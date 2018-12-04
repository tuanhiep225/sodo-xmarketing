/**
 * 
 */
package com.sodo.xmarketing.model.agency;
import java.util.List;

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
public class OrderModel {

	private String username;
	private String password;
	private List<OrderAgency> orders;
	
}
