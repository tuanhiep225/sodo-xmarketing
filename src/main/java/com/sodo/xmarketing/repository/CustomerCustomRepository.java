/**
 * 
 */
package com.sodo.xmarketing.repository;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.dto.CustomerSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.status.Role;

/**
 * @author tuanhiep225
 *
 */
public interface CustomerCustomRepository {

/**
 * @param param
 * @param keyword
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Customer> filterForCMS(String param, String keyword, PageRequest pageable, CurrentUser currentUser);

/**
 * @param query
 * @param numberRecord
 * @return
 * @throws SodException
 */
List<Customer> suggest(String query, int numberRecord) throws SodException;


/**
 * @param customerSearch
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Customer> filterV3(CustomerSearch customerSearch, PageRequest pageable, CurrentUser currentUser, Role role);

}
