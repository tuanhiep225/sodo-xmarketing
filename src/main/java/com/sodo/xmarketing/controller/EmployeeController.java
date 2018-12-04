/**
 * 
 */
package com.sodo.xmarketing.controller;

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.dto.ChargeModelDTO;
import com.sodo.xmarketing.dto.EmployeeCreationDTO;
import com.sodo.xmarketing.dto.EmployeeSearch;
import com.sodo.xmarketing.dto.EmployeeUpdateDTO;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.service.EmployeeService;
import com.sodo.xmarketing.status.CustomerStatus;
import com.sodo.xmarketing.utils.StringUtils;

/**
 * @author tuanhiep225
 *
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

	private static final Log LOGGER = LogFactory.getLog(EmployeeController.class);

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CurrentUserService currentUserService;

	@GetMapping("/{username}")
	public Employee getByUsername(@PathVariable("username") String username) {
		return employeeService.getByUsername(username);
	}

	@PostMapping("/charging/{customerCode}")
	public SodResult<Boolean> charge(@RequestBody ChargeModelDTO model,
			@PathVariable("customerCode") String customerCode, BindingResult errors) {
		if (errors.hasErrors()) {
			return SodResult.<Boolean>builder().isError(true).message(errors.getAllErrors().get(0).getDefaultMessage())
					.build();
		}
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if (currentUser.isCustomer()) {
			return SodResult.<Boolean>builder().isError(true).message("Current user not type Employee").build();
		}
		return employeeService.charge(customerCode, model, currentUser);
	}

	@PostMapping("/change-vip/{customerCode}")
	public SodResult<Boolean> changeVIP(@PathVariable("customerCode") String customerCode,
			@RequestParam("attribute") CustomerStatus attribute) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if (currentUser.isCustomer()) {
			return SodResult.<Boolean>builder().isError(true).message("Current user not type Employee").build();
		}
		return employeeService.chargeVIP(customerCode, attribute, currentUser);
	}

	@GetMapping("/filter")
	@Deprecated
	public SodSearchResult<Employee> filter(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestParam(value = "role", required = false) String role,
			@RequestParam(value = "param", required = false) String param,
			@RequestParam(value = "keyword", required = false) String keyword) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if (currentUser.isCustomer()) {
			return SodSearchResult.<Employee>builder().items(null).totalPages(0).totalRecord(0).build();
		}
		PageRequest pageable = PageRequest.of(page, pageSize);
		return employeeService.filter(param, keyword, pageable, currentUser, role);
	}

	@PostMapping("")
	// ("ROLE_ADMIN")
	public SodResult<Employee> create(@RequestBody @Valid Employee entity, BindingResult errors) {
		if (errors.hasErrors())
			return SodResult.<Employee>builder().isError(true).code(errors.getAllErrors().get(0).getCode())
					.message(errors.getAllErrors().get(0).getDefaultMessage()).build();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return employeeService.create(entity, currentUser);
	}

	@PostMapping("/{username}/role")
	// @Secured("ROLE_ADMIN")
	public SodResult<Boolean> updateRole(@RequestBody List<String> role, @PathVariable("username") String username,
			BindingResult errors) {
		if (errors.hasErrors())
			return SodResult.<Boolean>builder().isError(true).code(errors.getAllErrors().get(0).getCode())
					.message(errors.getAllErrors().get(0).getDefaultMessage()).build();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return employeeService.updateRole(role, username, currentUser);
	}

	@PutMapping("/{username}")
	public SodResult<Boolean> update(@RequestBody EmployeeUpdateDTO entity, @PathVariable("username") String username,
			BindingResult errors) {
		if (errors.hasErrors())
			return SodResult.<Boolean>builder().isError(true).code(errors.getAllErrors().get(0).getCode())
					.message(errors.getAllErrors().get(0).getDefaultMessage()).build();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return employeeService.update(entity, username, currentUser);
	}

	@PostMapping("/")
	// @Secured("ROLE_ADMIN")
	public SodResult<Employee> create(@RequestBody EmployeeCreationDTO entity, BindingResult errors)
			throws SodException {
		if (errors.hasErrors())
			return SodResult.<Employee>builder().isError(true).code(errors.getAllErrors().get(0).getCode())
					.message(errors.getAllErrors().get(0).getDefaultMessage()).build();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return employeeService.create(entity, currentUser);
	}

	/**
	 * Check tên đang nhập tồn tại
	 *
	 * @param username
	 *            Tên đăng nhập
	 * @return Kết quả
	 * @author tuanhiep225
	 */
	@GetMapping("/username/{username}/consist")
	public Boolean consistUsername(@PathVariable("username") String username) {
		return employeeService.existsByUsername(username);
	}

	/**
	 * Check email tồn tại
	 *
	 * @param email
	 *            Email cần kiểm tra
	 * @return Kết quả
	 * @author tuanhiep225
	 */
	@GetMapping("/email/{email}/consist")
	public Boolean consistEmail(@PathVariable("email") String email) {
		return employeeService.existsByEmail(email);
	}

	@PutMapping("/password")
	public SodResult<Boolean> ChangePassword(@RequestBody Map<String, String> pairPassword, BindingResult errors) {
		if (errors.hasErrors()) {
			LOGGER.error(errors);
			return null;
		}
		SodResult<Boolean> result = null;
		CurrentUser currentUser = currentUserService.getCurrentUser();
		result = employeeService.updatePassword(pairPassword, currentUser);
		return result;
	}

	@RequestMapping(value = "/suggest-employee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Employee>> suggestEmployee(@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "role", required = false) String role,
			@RequestParam(value = "numberRecord", required = false) int numberRecord) throws SodException {

		List<Employee> result = employeeService.suggest(StringUtils.unAccent(query).toLowerCase(), role, numberRecord);
		result.forEach(employee -> {
			employee.setName(employee.getName() + " - " + employee.getEmail());
		});
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PatchMapping("/customer/{customerCode}")
	public SodResult<Customer> assign(@RequestBody StaffDTO employee,
			@PathVariable("customerCode") String customerCode) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return employeeService.assign(employee, customerCode, currentUser);
	}

	@PostMapping("/filter")
	public SodSearchResult<Employee> filterV3(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestBody EmployeeSearch employeeSearch) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if (currentUser.isCustomer()) {
			return SodSearchResult.<Employee>builder().items(null).totalPages(0).totalRecord(0).build();
		}
		PageRequest pageable = PageRequest.of(page, pageSize);
		return employeeService.filterV3(employeeSearch,pageable, currentUser);
	}
}
