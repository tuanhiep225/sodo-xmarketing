/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.model.employee.Employee.EmployeeBuilder;

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
public class EmployeeUpdateDTO {

	private String name;
	private String address;
	private String phone;
	private Set<String> roles;
	private String email;
}
