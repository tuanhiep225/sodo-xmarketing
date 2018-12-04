/**
 *
 */
package com.sodo.xmarketing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.CustomerChargingDTO;
import com.sodo.xmarketing.dto.CustomerSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.config.Domain;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.wallet.TargetObject;
import com.sodo.xmarketing.model.wallet.TargetObjectType;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.model.wallet.Wallet;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.CustomerRepository;
import com.sodo.xmarketing.service.CustomerService;
import com.sodo.xmarketing.service.DomainService;
import com.sodo.xmarketing.service.WalletTransactionService;
import com.sodo.xmarketing.status.CustomerStatus;
import com.sodo.xmarketing.status.Role;

import lombok.var;

/**
 * @author tuanhiep225
 */
@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Log LOGGER = LogFactory.getLog(CustomerServiceImpl.class);
	@Autowired
	CustomerRepository customerRepo;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private NextSequenceService sequenceService;

	@Autowired
	private DomainService domainService;
	
	  @Autowired
	  private PasswordEncoder passwordEncoder;
	  
	  @Autowired
	  private WalletTransactionService walletTransactionService; 

	@Override
	public SodResult<Customer> create(Customer customer, String domain) throws SodException {

		SodResult<Customer> result = new SodResult<>();

		if (customer.getFormat() == null || Strings.isNullOrEmpty(customer.getFormat().getLang())) {
			result.setError(true);
			result.setCode("TYPE_MONEY");
			result.setMessage("Vui lòng chọn loại tiền tệ");
			return result;
		}

		if (customerRepo.findByUsername(customer.getUsername()) != null) {
			result.setError(true);
			result.setCode("USER_NAME_EXISTS");
			result.setMessage("Tên đăng nhập đã tồn tại");
			return result;
		}

		if (customerRepo.findByEmail(customer.getEmail()) != null) {
			result.setError(true);
			result.setCode("EMAIL_EXISTS");
			result.setMessage("Email đã tồn tại");
			return result;
		}

		Domain domainCustomer = domainService.findOneByDomainName(domain);

		if (domainCustomer == null) {
			result.setError(true);
			result.setCode("DOMAIN_NOT_EXISTS");
			result.setMessage("Cấu hình hình tên miền không tồn tại");
			return result;
		}

		List<Format> formats = domainService.getFormatsOfCmsByDomainName(domain);

		if (formats == null || formats.isEmpty()) {
			result.setError(true);
			result.setCode("DOMAIN_NOT_EXISTS");
			result.setMessage("Cấu hình hình tên miền không tồn tại");
			return result;
		}

		// Set cấu hình tiền tệ mặc định cho tài khoản đăng ký
		formats.stream()
				.filter(format -> !Strings.isNullOrEmpty(format.getLang())
						&& format.getLang().equals(customer.getFormat().getLang()))
				.findFirst().ifPresent(customer::setFormat);

		customer.setCode(sequenceService.genCustomerCode(domainCustomer.getCodePrefix()));

		customer.setEnabled(true);
		customer.setPassword(new BCryptPasswordEncoder().encode(customer.getPassword()));
		customer.setBalance(BigDecimal.ZERO);
		customer.setBalanceLife(BigDecimal.ZERO);
		customer.setAttribute(CustomerStatus.NORMAL);

		result.setResult(customerRepo.add(customer));

		if (result.getResult() == null) {
			result.setError(true);
			result.setCode("REGISTER_ERROR");
			result.setMessage("Đăng ký không thành công");
			return result;
		}

		return result;
	}

	@Override
	public Customer getById(String id) {
		return customerRepo.get(id);
		// return null;
	}

	@Override
	public List<Customer> getAll() {
		return (List<Customer>) customerRepo.getAll();
		// return null;
	}

	@Override
	public Boolean removeById(String id) {
		return customerRepo.remove(id);
		// return null;
	}

	@Override
	public Customer findByEmail(String email) {
		return customerRepo.findByEmail(email);
		// return null;
	}

	/**
	 * @Henry Kiểm tra tên đã nhập đã tồn tại
	 * @param username
	 *            tên đăng nhập cần kiểm tra
	 * @return True nếu tồn tại
	 */
	@Override
	public Boolean existsByUsername(String username) {
		return customerRepo.existsByUsername(username);
	}

	/**
	 * @Henry Kiểm tra tên đã nhập đã tồn tại
	 * @param email
	 *            tên đăng nhập cần kiểm tra
	 * @return True nếu tồn tại
	 */
	@Override
	public Boolean existsByEmail(String email) {
		return customerRepo.existsByEmail(email);
	}

	@Override
	public Boolean findByUsernameIgnoreIsDelete(String username) {
		Customer customer = null;
		try {
			customer = customerRepo.findByUsernameIgnoreIsDelete(username);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		if (null == customer) {
			return false;
		}
		return true;
	}

	@Override
	public Customer findByUsername(String username) {
		Customer customer = null;
		try {
			customer = customerRepo.findByUsername(username);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return customer;
	}

	@Override
	public Customer updateProfile(Customer customer, String id) {
		if (null == id || id.isEmpty()) {
			LOGGER.error("Paramater 'id' must be not null or empty !");
			return null;
		}
		if (null == customer) {
			LOGGER.error("Paramater 'customer' must be not null!");
			return null;
		}

		Customer rs = null;
		rs = customerRepo.get(id);
		if (null == rs) {
			return rs;
		}

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		if (!customer.getName().isEmpty()) {
			update.set("name", customer.getName());
		}
		if (!customer.getPhone().isEmpty()) {
			update.set("phone", customer.getPhone());
		}

		if (customer.getDateOfBirth() != null) {
			update.set("dateOfBirth", customer.getDateOfBirth());
		}
		if (!customer.getAddress().isEmpty()) {
			update.set("address", customer.getAddress());
		}
		// if (!customer.getPassword().isEmpty()) {
		// update.set("password", new
		// BCryptPasswordEncoder().encode(customer.getPassword()));
		// }
		try {
			rs = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true),
					Customer.class);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.CustomerService#updateBalance(java.lang.String,
	 * java.math.BigDecimal)
	 */
	@Override
	public Customer updateBalance(String id, BigDecimal balance, TransactionType type) {
		if (null == id || id.isEmpty()) {
			LOGGER.error("Paramater 'id' must be not null or empty !");
			return null;
		}

		Customer rs = null;
		rs = customerRepo.get(id);
		if (null == rs) {
			return rs;
		}
		var currentBalance = rs.getBalance();
		if(type.equals(TransactionType.CREDIT))
			currentBalance = currentBalance.subtract(balance);
		else if (type.equals(TransactionType.DEBIT))
			currentBalance = currentBalance.add(balance);
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id).and("isDelete").is(false));
		Update update = new Update();
		update.set("balance", currentBalance);
		try {
			rs = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true),
					Customer.class);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.CustomerService#updateBalanceLife(java.lang.
	 * String, java.math.BigDecimal)
	 */
	@Override
	public Customer updateBalanceLife(String id, BigDecimal balance,TransactionType type) {
		if (null == id || id.isEmpty()) {
			LOGGER.error("Paramater 'id' must be not null or empty !");
			return null;
		}

		Customer rs = null;
		rs = customerRepo.get(id);
		if (null == rs) {
			return rs;
		}

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id).and("isDelete").is(false));
		Update update = new Update();
		var currentBalanceLife = rs.getBalanceLife();
		if(type.equals(TransactionType.CREDIT))
			currentBalanceLife = currentBalanceLife.subtract(balance);
		else if (type.equals(TransactionType.DEBIT))
			currentBalanceLife = currentBalanceLife.add(balance);
		update.set("balanceLife", currentBalanceLife);
		try {
			rs = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true),
					Customer.class);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.CustomerService#updatePassword(java.util.Map)
	 */
	@Override
	public SodResult<Boolean> updatePassword(Map<String, String> pairPassword, CurrentUser currentUser) {
		if (null == currentUser) {
			LOGGER.error("CUSTOMER_INVALID, Customer invalid !");
			return SodResult.<Boolean>builder().isError(true).message("Customer invalid !").code("CUSTOMER_INVALID")
					.build();
		}
		Customer rs = null;
		rs = customerRepo.findByUsername(currentUser.getUserName());
		if (rs == null) {
			LOGGER.error("CUSTOMER_INVALID, Customer invalid !");
			return SodResult.<Boolean>builder().isError(true).message("Customer invalid !").code("CUSTOMER_INVALID")
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
			rs = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true),
					Customer.class);
			return SodResult.<Boolean>builder().isError(false).message("Change password success !").build();
		} catch (Exception ex) {
			return SodResult.<Boolean>builder().isError(true).message(ex.getMessage()).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.CustomerService#filterForCMS(java.lang.String, java.lang.String, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Customer> filterForCMS(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser) {
		return customerRepo.filterForCMS(param, keyword, pageable, currentUser);
		
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.CustomerService#charge(java.lang.String, com.sodo.xmarketing.dto.CustomerChargingDTO)
	 */
	@Override
	public SodResult<Customer> charge(String code, CustomerChargingDTO customerCharging, CurrentUser currentUser) {
		Customer customer = customerRepo.findByUsername(code);
		
		
		// update tài khoản khách hàng
		Customer rs = updateBalance(customer.getId(), customerCharging.getBalance(), TransactionType.CREDIT);
				
		var afterBalance = rs.getBalance();
		updateBalanceLife(customer.getId(), customerCharging.getBalance(), TransactionType.DEBIT).getBalance();

		// Create wallet-transaction

		WalletTransaction wallet = WalletTransaction.builder().amount(customerCharging.getBalance())
				.beforeAmount(customer.getBalance()).afterAmount(afterBalance)
				.wallet(Wallet.builder().customerCode(customer.getCode()).customerEmail(customer.getEmail())
						.customerUserName(customer.getUsername()).customerName(customer.getName()).build())
				.target(TargetObject.builder().code(code).type(TargetObjectType.CUSTOMER).build())
				.code(sequenceService.genWalletTransactionCode()).date(LocalDate.now()).build();
		walletTransactionService.create(wallet, currentUser);
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.CustomerService#suggest(java.lang.String, int)
	 */
	@Override
	public List<Customer> suggest(String query, int numberRecord) throws SodException {
		return customerRepo.suggest(query, numberRecord);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.CustomerService#filterV3(com.sodo.xmarketing.dto.CustomerSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Customer> filterV3(CustomerSearch customerSearch, PageRequest pageable,
			CurrentUser currentUser, Role role) {
		return customerRepo.filterV3(customerSearch,pageable,currentUser,role);
	}
}
