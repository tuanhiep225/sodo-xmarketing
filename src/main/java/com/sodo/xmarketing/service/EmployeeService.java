/**
 * 
 */
package com.sodo.xmarketing.service;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.dto.ChargeModelDTO;
import com.sodo.xmarketing.dto.EmployeeCreationDTO;
import com.sodo.xmarketing.dto.EmployeeSearch;
import com.sodo.xmarketing.dto.EmployeeUpdateDTO;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.status.CustomerStatus;

/**
 * @author tuanhiep225
 *
 */
public interface EmployeeService {
	public Employee getByUsername(String username);

	/**
	 * @param customerCode
	 * @param model
	 * @param currentUser
	 * @return
	 */
	public SodResult<Boolean> charge(String customerCode, ChargeModelDTO model, CurrentUser currentUser);

	/**
	 * @param customerCode
	 * @param currentUser
	 * @return
	 */
	public SodResult<Boolean> chargeVIP(String customerCode, CustomerStatus attribute, CurrentUser currentUser);

	/**
	 * @param param
	 * @param keyword
	 * @param pageable
	 * @param currentUser
	 * @param role
	 * @return
	 */
	public SodSearchResult<Employee> filter(String param, String keyword, PageRequest pageable, CurrentUser currentUser,
			String role);

	/**
	 * @param entity
	 * @param currentUser 
	 * @return
	 */
	public SodResult<Employee> create(Employee entity, CurrentUser currentUser);

	/**
	 * @param role
	 * @param username
	 * @param currentUser
	 * @return
	 */
	public SodResult<Boolean> updateRole(List<String> role, String username, CurrentUser currentUser);

	/**
	 * @param entity
	 * @param username
	 * @param currentUser
	 * @return
	 */
	public SodResult<Boolean> update(EmployeeUpdateDTO entity, String username, CurrentUser currentUser);

	/**
	 * @param entity
	 * @param currentUser
	 * @return
	 * @throws SodException 
	 */
	public SodResult<Employee> create(EmployeeCreationDTO entity, CurrentUser currentUser) throws SodException;
	
	
	public  Boolean existsByUsername(String username);

	public  Boolean existsByEmail(String email);

	/**
	 * @param pairPassword
	 * @param currentUser
	 * @return
	 */
	public SodResult<Boolean> updatePassword(Map<String, String> pairPassword, CurrentUser currentUser);

	/**
	 * @param role 
	 * @param lowerCase
	 * @param numberRecord
	 * @return
	 */
	 public List<Employee> suggest(String query, String role, int numberRecord) throws SodException;

	/**
	 * @param employee
	 * @param customerCode 
	 * @param currentUser
	 * @return
	 */
	public SodResult<Customer> assign(StaffDTO employee, String customerCode, CurrentUser currentUser);

	/**
	 * @param employeeSearch
	 * @param pageable
	 * @param currentUser
	 * @return
	 */
	public SodSearchResult<Employee> filterV3(EmployeeSearch employeeSearch, PageRequest pageable,
			CurrentUser currentUser);
	
}
