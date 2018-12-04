/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.util.Set;

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
public class EmployeeSearch {

	private String code;
	private String username;
	private String phone;
	private String name;
	private String email;
	private Set<String> roles;
}
