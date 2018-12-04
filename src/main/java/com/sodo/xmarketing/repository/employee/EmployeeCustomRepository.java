/**
 * 
 */
package com.sodo.xmarketing.repository.employee;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.dto.EmployeeSearch;
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
public interface EmployeeCustomRepository {

	/**
	 * @param customerCode
	 * @param currentUser
	 * @return
	 */
	SodResult<Boolean> changeVIP(String customerCode, CustomerStatus attribute, CurrentUser currentUser);

	/**
	 * @param param
	 * @param keyword
	 * @param pageable
	 * @param currentUser
	 * @param role
	 * @return
	 */
	SodSearchResult<Employee> filter(String param, String keyword, PageRequest pageable, CurrentUser currentUser,
			String role);

	/**
	 * @param query
	 * @param role
	 * @param numberRecord
	 * @return
	 * @throws SodException
	 */
	List<Employee> suggest(String query, String role, int numberRecord) throws SodException;

	/**
	 * @param employee
	 * @param customerCode
	 * @param currentUser
	 * @return
	 */
	Customer assignSaleToCustomer(StaffDTO employee, String customerCode, CurrentUser currentUser);

	Boolean assignSaleToOrder(StaffDTO employee, String username, CurrentUser currentUser);

	/**
	 * @param employeeSearch
	 * @param pageable
	 * @param currentUser
	 * @return
	 */
	SodSearchResult<Employee> filterV3(EmployeeSearch employeeSearch, PageRequest pageable, CurrentUser currentUser);

}
