/**
 *
 */
package com.sodo.xmarketing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.dto.CustomerChargingDTO;
import com.sodo.xmarketing.dto.CustomerSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.status.Role;

/**
 * @author tuanhiep225
 */
public interface CustomerService {

  SodResult<Customer> create(Customer customer, String domain) throws SodException;

  Customer getById(String id);

  List<Customer> getAll();

  Boolean removeById(String id);

  Customer findByEmail(String email);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean findByUsernameIgnoreIsDelete(String username);

  Customer findByUsername(String username);

  Customer updateProfile(Customer customer, String id);

  Customer updateBalance(String id, BigDecimal balance, TransactionType type);

  Customer updateBalanceLife(String id, BigDecimal balance, TransactionType type);

/**
 * @param pairPassword
 * @return true or false
 */
  SodResult<Boolean> updatePassword(Map<String, String> pairPassword, CurrentUser currentUser);

/**
 * @param param
 * @param keyword
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Customer> filterForCMS(String param, String keyword, PageRequest pageable, CurrentUser currentUser);

/**
 * @param code
 * @param customerCharging
 * @return
 */
SodResult<Customer> charge(String code, CustomerChargingDTO customerCharging, CurrentUser currentUser);

/**
 * @param lowerCase
 * @param numberRecord
 * @return
 * @throws SodException 
 */
List<Customer> suggest(String lowerCase, int numberRecord) throws SodException;

/**
 * @param customerSearch
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Customer> filterV3(CustomerSearch customerSearch, PageRequest pageable, CurrentUser currentUser, Role role);
}
