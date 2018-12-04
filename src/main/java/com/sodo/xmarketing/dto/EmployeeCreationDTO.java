/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.dto.EmployeeUpdateDTO.EmployeeUpdateDTOBuilder;

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
public class EmployeeCreationDTO {
	private String name;
	private String address;
	private String phone;
	private Set<String> roles;
	private String username;
	private String email;
}
