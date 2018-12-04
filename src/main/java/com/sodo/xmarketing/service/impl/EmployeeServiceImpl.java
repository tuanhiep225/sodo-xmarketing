/**
 * 
 */
package com.sodo.xmarketing.service.impl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TargetObject;
import com.sodo.xmarketing.model.wallet.TargetObjectType;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.model.wallet.Wallet;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.CustomerRepository;
import com.sodo.xmarketing.repository.employee.EmployeeRepository;
import com.sodo.xmarketing.repository.wallet.WalletDeterminantRepository;
import com.sodo.xmarketing.service.CustomerService;
import com.sodo.xmarketing.service.EmployeeService;
import com.sodo.xmarketing.service.WalletTransactionService;
import com.sodo.xmarketing.status.CustomerStatus;
import com.sodo.xmarketing.status.Role;
import com.sodo.xmarketing.status.SystemDeterminant;
import com.sodo.xmarketing.utils.AccountEntryContent;
import com.sodo.xmarketing.utils.ConfigHelper;

import lombok.var;

/**
 * @author tuanhiep225
 *
 */
@Service
public class EmployeeServiceImpl implements EmployeeService{
	
	@Autowired
	private EmployeeRepository repository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private NextSequenceService sequenceService;
	@Autowired
	private WalletTransactionService walletTransactionService; 
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private ConfigHelper configHelper;
	
	@Autowired
	private WalletDeterminantRepository walletDeterminant;

	private static final Log LOGGER = LogFactory.getLog(EmployeeServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#getByUsername(java.lang.String)
	 */
	@Override
	public Employee getByUsername(String username) {
		return repository.findByUsername(username);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#charge(java.lang.String, com.sodo.xmarketing.dto.ChargeModelDTO, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> charge(String customerCode, ChargeModelDTO model, CurrentUser currentUser) {
		
		Employee employee = repository.findByUsername(currentUser.getUserName());
		if (!passwordEncoder.matches(model.getPassword(), employee.getPassword())) {
			LOGGER.error("PASSWORD_NOT_MATCH, Password not match !");
			return SodResult.<Boolean>builder().isError(true).message("Password not match !").code("PASSWORD_NOT_MATCH")
					.build();
		}
		
		Customer customer = customerRepository.findOneByCodeIgnoreCaseAndIsDelete(customerCode, false);
		if(customer == null)
			return SodResult.<Boolean>builder().isError(true).message("Customer missing").code("CUSTOMER_MISSING")
					.build();
	    AccountEntryContent accountEntryContent =
	            configHelper.getConfig(AccountEntryContent.class);
	    
		// update tài khoản khách hàng
		Customer rs = customerService.updateBalance(customer.getId(), model.getTotal(), TransactionType.DEBIT);
				
		var afterBalance = rs.getBalance();

		// Create wallet-transaction

		var determinant = walletDeterminant.findByCode(SystemDeterminant.N1.toString());
		
		WalletTransaction wallet = WalletTransaction.builder().amount(model.getTotal())
				.beforeAmount(customer.getBalance())
				.afterAmount(afterBalance)
				.wallet(Wallet.builder().customerCode(customer.getCode()).customerEmail(customer.getEmail())
						.customerUserName(customer.getUsername()).customerName(customer.getName()).build())
				.target(TargetObject.builder().code(customerCode).type(TargetObjectType.CUSTOMER).build())
				.code(sequenceService.genWalletTransactionCode()).date(LocalDate.now())
				.type(TransactionType.DEBIT)
				.walletDeterminant(Determinant.builder().code(determinant.getCode()).name(determinant.getName()).treeCode(determinant.getTreeCode()).build())
				.status(TransactionStatus.COMPLETED)
				.format(customer.getFormat())
				.content(String.format(accountEntryContent.getDepositRequest())).build();
		walletTransactionService.create(wallet, currentUser);
		return SodResult.<Boolean>builder().result(true).build();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#chargeVIP(java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> chargeVIP(String customerCode, CustomerStatus attribute, CurrentUser currentUser) {
		return repository.changeVIP(customerCode,attribute, currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#filter(java.lang.String, java.lang.String, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser, java.lang.String)
	 */
	@Override
	public SodSearchResult<Employee> filter(String param, String keyword, PageRequest pageable, CurrentUser currentUser,
			String role) {
		return repository.filter(param, keyword, pageable, currentUser, role);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#create(com.sodo.xmarketing.model.employee.Employee, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Employee> create(Employee entity, CurrentUser currentUser) {
		entity.setCreatedBy(currentUser.getUserName());
		entity.setCreatedDate(LocalDateTime.now());
		try {
			var rs=  repository.add(entity);
			return SodResult.<Employee>builder().result(rs).build();
		} catch (Exception e) {
			return SodResult.<Employee>builder().isError(true).message(e.getMessage()).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#updateRole(java.util.List, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> updateRole(List<String> roles, String username, CurrentUser currentUser) {
		
		var employee = repository.findByUsername(username);
		if(employee == null)
			return SodResult.<Boolean>builder().result(false).isError(true).build();
		employee.setRoles(roles.stream().collect(Collectors.toSet()));
		var rs = repository.update(employee);
		if(rs == null)
			return SodResult.<Boolean>builder().result(false).isError(true).build();
		return SodResult.<Boolean>builder().result(true).isError(false).build();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#update(com.sodo.xmarketing.dto.EmployeeUpdateDTO, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> update(EmployeeUpdateDTO entity, String username, CurrentUser currentUser) {
		var employee = repository.findByUsername(username);
		if(employee == null)
			return SodResult.<Boolean>builder().isError(true).message("Can't found employee !").build();
		employee.setName(entity.getName());
		employee.setAddress(entity.getAddress());
		employee.setPhone(entity.getPhone());
		if(entity.getRoles().size()>0) {
			employee.setRoles(entity.getRoles());
		} else {
			Set<String> roles = new HashSet<>();
			roles.add(Role.ROLE_STAFF.toString());
			employee.setRoles(roles);
		}
		try {
			var rs = repository.update(employee);
			if(rs == null)
				 return SodResult.<Boolean>builder().isError(true).result(false).build();
			return SodResult.<Boolean>builder().isError(false).result(true).build();
		} catch (Exception e) {
			return SodResult.<Boolean>builder().isError(true).result(false).message(e.getMessage()).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#create(com.sodo.xmarketing.dto.EmployeeCreationDTO, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Employee> create(EmployeeCreationDTO entity, CurrentUser currentUser) throws SodException {
		Employee employee = new Employee();
		employee.setUsername(entity.getUsername());
		employee.setEmail(entity.getEmail());
		employee.setName(entity.getName());
		employee.setAddress(entity.getAddress());
		employee.setPhone(entity.getPhone());
		employee.setPassword(new BCryptPasswordEncoder().encode("sodo123456"));
		employee.setCreatedBy(currentUser.getUserName());
		employee.setCreatedDate(LocalDateTime.now());
		employee.setCode(sequenceService.genEmployeeCode());
		employee.setEnabled(true);
		if(entity.getRoles().size() > 0)
		{
			employee.setRoles(entity.getRoles());
		}
		else
		{
			Set<String> roles = new HashSet<>();
			roles.add(Role.ROLE_STAFF.toString());
			employee.setRoles(roles);
		}
			
		try {
			var rs = repository.add(employee);
			return SodResult.<Employee>builder().result(rs).build();
		} catch (Exception e) {
			return SodResult.<Employee>builder().isError(true).message(e.getMessage()).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#existsByUsername(java.lang.String)
	 */
	@Override
	public Boolean existsByUsername(String username) {
		return repository.existsByUsername(username);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#existsByEmail(java.lang.String)
	 */
	@Override
	public Boolean existsByEmail(String email) {
		return repository.existsByEmail(email);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#updatePassword(java.util.Map, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> updatePassword(Map<String, String> pairPassword, CurrentUser currentUser) {
		if (null == currentUser) {
			LOGGER.error("EMPLOYEE_INVALID, Employee invalid !");
			return SodResult.<Boolean>builder().isError(true).message("Employee invalid !").code("EMPLOYEE_INVALID")
					.build();
		}
		Employee rs = null;
		rs = repository.findByUsername(currentUser.getUserName());
		if (rs == null) {
			LOGGER.error("EMPLOYEE_INVALID, Employee invalid !");
			return SodResult.<Boolean>builder().isError(true).message("Customer invalid !").code("EMPLOYEE_INVALID")
					.build();
		}

		String encodePasswordOld = new BCryptPasswordEncoder().encode(pairPassword.get("passwordOld"));

		if (!passwordEncoder.matches(pairPassword.get("passwordOld"), rs.getPassword())) {
			LOGGER.error("PASSWORD_OLD, Password old not match !");
			return SodResult.<Boolean>builder().isError(true).message("Password old not match !").code("PASSWORD_OLD_NOT_MATCH")
					.build();
		}

		Query query = new Query();
		Update update = new Update();
		query.addCriteria(Criteria.where("id").is(rs.getId()));
		update.set("password", new BCryptPasswordEncoder().encode(pairPassword.get("passwordNew")));
		try {
			mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true),
					Employee.class);
			return SodResult.<Boolean>builder().isError(false).message("Change password success !").build();
		} catch (Exception ex) {
			return SodResult.<Boolean>builder().isError(true).message(ex.getMessage()).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#suggest(java.lang.String, int)
	 */
	@Override
	public List<Employee> suggest(String query, String role, int numberRecord) throws SodException {
		 return repository.suggest(query,role, numberRecord);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#assign(com.sodo.xmarketing.dto.StaffDTO, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Customer> assign(StaffDTO employee, String customerCode, CurrentUser currentUser) {

		try {
			var rs = repository.assignSaleToCustomer(employee,customerCode,currentUser);
			repository.assignSaleToOrder(employee, rs.getUsername(), currentUser);
			return SodResult.<Customer>builder().result(rs).build();
		} catch (Exception e) {
			return SodResult.<Customer>builder().isError(true).message(e.getMessage()).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.EmployeeService#filterV3(com.sodo.xmarketing.dto.EmployeeSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Employee> filterV3(EmployeeSearch employeeSearch, PageRequest pageable,
			CurrentUser currentUser) {
		return repository.filterV3(employeeSearch,pageable, currentUser);
	}

}
