/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.dto.EmployeeSearch.EmployeeSearchBuilder;
import com.sodo.xmarketing.status.CustomerStatus;

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
public class CustomerSearch {

	private String code;
	private String username;
	private String phone;
	private String name;
	private String email;
	private String level;
}
