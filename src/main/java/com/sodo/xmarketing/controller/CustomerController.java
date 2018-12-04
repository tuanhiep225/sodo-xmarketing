/**
 *
 */
package com.sodo.xmarketing.controller;

import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.dto.CustomerChargingDTO;
import com.sodo.xmarketing.dto.CustomerSearch;
import com.sodo.xmarketing.dto.EmployeeSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.service.CustomerService;
import com.sodo.xmarketing.status.Role;
import com.sodo.xmarketing.utils.StringUtils;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tuanhiep225
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

  private static final Log LOGGER = LogFactory.getLog(CustomerController.class);

  @Autowired
  private CustomerService customerService;
  
  @Autowired
  private CurrentUserService currentUserService;
  

  @PostMapping("/register/{domain}")
  public Customer register(@Valid @RequestBody Customer customer,
      @PathVariable String domain,
      BindingResult errors) throws SodException {

    if (errors.hasErrors()) {
      throw new SodException("Vui lòng kiểm tra lại dữ liệu", "INPUT_ERROR");
    }

    SodResult<Customer> result = customerService.create(customer, domain);

    if (result.isError()) {
      throw new SodException(result.getMessage(), result.getCode());
    }

    return result.getResult();
  }

  @GetMapping("/exist/{username}")
  public Boolean checkUserName(@PathVariable("username") String username) {
    return customerService.findByUsernameIgnoreIsDelete(username);
  }

  /**
   * Check tên đang nhập tồn tại
   *
   * @param username Tên đăng nhập
   * @return Kết quả
   * @author Henry
   */
  @GetMapping("/username/consist")
  public Boolean consistUsername(@RequestParam("username") String username) {
    return customerService.existsByUsername(username);
  }

  /**
   * Check email tồn tại
   *
   * @param email Email cần kiểm tra
   * @return Kết quả
   * @author Henry
   */
  @GetMapping("/email/consist")
  public Boolean consistEmail(@RequestParam("email") String email) {
    return customerService.existsByEmail(email);
  }

  @GetMapping("/{username}")
  public Customer findByUserName(@PathVariable("username") String username) {
    return customerService.findByUsername(username);
  }

  @PutMapping("/{id}")
  public Customer update(@RequestBody Customer customer, @PathVariable("id") String id,
      BindingResult errors) {
    if (errors.hasErrors()) {
      LOGGER.error(errors);
      return null;
    }
    Customer result = null;
    result = customerService.updateProfile(customer, id);
    return result;
  }
  
  @PutMapping("/password")
  public SodResult<Boolean> ChangePassword(@RequestBody Map<String, String> pairPassword, BindingResult errors) {
    if (errors.hasErrors()) {
      LOGGER.error(errors);
      return null;
    }
    SodResult<Boolean> result = null;
    CurrentUser currentUser = currentUserService.getCurrentUser();
    result = customerService.updatePassword(pairPassword,currentUser);
    return result;
  }
  
  @GetMapping("/cms/filter")
  @Deprecated
  public SodSearchResult<Customer> filter(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestParam(value = "param", required = false) String param,
			@RequestParam(value = "keyword", required = false) String keyword) {
	  CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser.isCustomer()) {
			return SodSearchResult.<Customer>builder().items(null).totalPages(0).totalRecord(0).build();
		}
	PageRequest pageable = PageRequest.of(page, pageSize);
	return customerService.filterForCMS(param, keyword, pageable, currentUser);
  }
  
  @PostMapping("/cms/charging/{code}")
  public SodResult<Customer> charge(@Valid @RequestBody CustomerChargingDTO customerCharging,
      @PathVariable("code") String code,
      BindingResult errors) {

    if (errors.hasErrors()) {
    	return SodResult.<Customer>builder().isError(true).message("Please check data!").code("INPUT_ERROR").build();
    }
    
    CurrentUser currentUser = currentUserService.getCurrentUser();
    
    if(currentUser == null) {
    	return SodResult.<Customer>builder().isError(true).message("Current user not found!").code("CURRENT_USER_MISSING").build();
    }

    SodResult<Customer> result = customerService.charge(code,customerCharging,currentUser);


    return result;
  }
  
  @RequestMapping(value = "/suggest-customer", method = RequestMethod.GET,
	      produces = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<List<Customer>> suggestEmployee(
	      @RequestParam(value = "query", required = false) String query,
	      @RequestParam(value = "numberRecord", required = false) int numberRecord)
	      throws SodException {

	    List<Customer> result =
	    		customerService.suggest(StringUtils.unAccent(query).toLowerCase(), numberRecord);
	    result.forEach(customer -> {
	    	customer.setName(customer.getName() + " - " + customer.getEmail());
	    });
	    return new ResponseEntity<>(result, HttpStatus.OK);
	  }
  
  @PostMapping("/cms/manage-filter")
  public SodSearchResult<Customer> manageFilter(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestBody CustomerSearch customerSearch) {
	  CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser.isCustomer()) {
			return SodSearchResult.<Customer>builder().items(null).totalPages(0).totalRecord(0).build();
		}
	PageRequest pageable = PageRequest.of(page, pageSize);
	return customerService.filterV3(customerSearch, pageable, currentUser, Role.ROLE_ADMIN);
  }
  
  @PostMapping("/cms/sale-filter")
  public SodSearchResult<Customer> saleFilter(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestBody CustomerSearch customerSearch) {
	  CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser.isCustomer()) {
			return SodSearchResult.<Customer>builder().items(null).totalPages(0).totalRecord(0).build();
		}
	PageRequest pageable = PageRequest.of(page, pageSize);
	return customerService.filterV3(customerSearch, pageable, currentUser, Role.ROLE_SALE);
  }
  
}
