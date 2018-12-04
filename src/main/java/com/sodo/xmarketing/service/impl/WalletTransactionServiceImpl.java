package com.sodo.xmarketing.service.impl;

import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.AccountingEntryFilter;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.HistoryTransaction;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.TransactionChain;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TargetObject;
import com.sodo.xmarketing.model.wallet.TargetObjectType;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.model.wallet.Wallet;
import com.sodo.xmarketing.model.wallet.WalletDeterminant;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.CustomerRepository;
import com.sodo.xmarketing.repository.TransactionChainRepository;
import com.sodo.xmarketing.repository.wallet.WalletDeterminantRepository;
import com.sodo.xmarketing.repository.wallet.WalletTransactionRepository;
import com.sodo.xmarketing.service.CustomerService;
import com.sodo.xmarketing.service.InitDataHelper;
import com.sodo.xmarketing.service.WalletTransactionService;
import com.sodo.xmarketing.status.SystemDeterminant;
import com.sodo.xmarketing.utils.AccountEntryContent;
import com.sodo.xmarketing.utils.ConfigHelper;
import com.sodo.xmarketing.utils.ErrorCode;
import com.sodo.xmarketing.utils.FunctionalUtils;
import com.sodo.xmarketing.utils.Properties;
import com.sodo.xmarketing.utils.TransactionChainStatus;
import com.sodo.xmarketing.utils.TranslateKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Created by Henry Do User: henrydo Date: 15/08/2018 Time: 14/56
 */
@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {

	@Autowired
	private WalletTransactionRepository repository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private NextSequenceService nextSequence;

	private InitDataHelper initDataHelper;

	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private WalletDeterminantRepository walletDeterminantRepo;

	@Autowired
	TransactionChainRepository transactionChainRepository;

	@Autowired
	public WalletTransactionServiceImpl(Properties properties, MongoTemplate mongoTemplate,
			ConfigHelper configHelper) {
		initDataHelper = new InitDataHelper(properties, mongoTemplate, configHelper);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sodo.xmarketing.service.WalletTransactionService#createFromOrder(java.
	 * util.Collection, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Collection<WalletTransaction>> createFromOrder(Collection<Order> entities,
			CurrentUser curentUser) {
		Customer customer = null;
		Collection<WalletTransaction> models = new ArrayList<WalletTransaction>();
		if (curentUser == null) {
			return SodResult.<Collection<WalletTransaction>>builder().isError(true)
					.message("Current user must not be null").code("NULL_CURRENT_USER").build();
		}
		customer = customerRepository.findByUsername(curentUser.getUserName());
		if (customer == null) {
			return SodResult.<Collection<WalletTransaction>>builder().isError(true)
					.message("Can not found customer in system.").code("CUSTOEMR_MISSING").build();
		}

		BigDecimal priceOrders =
				entities.stream().map(x -> x.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (customer.getBalance().compareTo(priceOrders) < 0) {
			return SodResult.<Collection<WalletTransaction>>builder().isError(true)
					.message("Balance not enough !").code("BALANCE_NOT_ENOUGH").build();
		}

		SodResult<Collection<WalletTransaction>> result = new SodResult<>();

		for (var order : entities) {
			SodResult<WalletTransaction> rs = addSingleWalletTransaction(order, curentUser);
			if (!rs.isError()) {
				models.add(rs.getResult());
			}
		}
		result.setResult(models);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sodo.xmarketing.service.WalletTransactionService#createFromOrder(com.sodo
	 * .xmarketing.model. Order, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<WalletTransaction> createFromOrder(Order entities, CurrentUser curentUser) {

		Customer customer = null;
		if (curentUser == null) {
			return SodResult.<WalletTransaction>builder().isError(true)
					.message("Current user must not be null").code("NULL_CURRENT_USER").build();
		}
		customer = customerRepository.findByUsername(curentUser.getUserName());
		if (customer == null) {
			return SodResult.<WalletTransaction>builder().isError(true)
					.message("Can not found customer in system.").code("CUSTOEMR_MISSING").build();
		}

		if (customer.getBalance().compareTo(entities.getPrice()) < 0) {
			return SodResult.<WalletTransaction>builder().isError(true)
					.message("Balance not enough !").code("BALANCE_NOT_ENOUGH").build();
		}

		return addSingleWalletTransaction(entities, curentUser);
	}

	/**
	 * @param entities
	 * @param curentUser
	 * @param customer
	 * @return
	 */
	private SodResult<WalletTransaction> addSingleWalletTransaction(Order entities,
			CurrentUser curentUser) {

		AccountEntryContent accountEntryContent = configHelper.getConfig(AccountEntryContent.class);

		var determinant = walletDeterminantRepo.findByCode(SystemDeterminant.ORDER_PAY.toString());

		Customer customer = customerRepository.findByUsername(curentUser.getUserName());
		WalletTransaction payment = new WalletTransaction();
		payment.setCode(nextSequence.genWalletTransactionCode());
		payment.setTarget(TargetObject.builder().code(entities.getCode())
				.type(TargetObjectType.ORDER).build());
		payment.setWallet(Wallet.builder().customerCode(curentUser.getCode())
				.customerEmail(curentUser.getEmail()).customerUserName(curentUser.getUserName())
				.customerName(curentUser.getFullName()).build());
		payment.setCreatedDate(LocalDateTime.now());
		payment.setType(TransactionType.CREDIT);
		payment.setAmount(entities.getPrice());
		payment.setBeforeAmount(customer.getBalance());
		var balanceAfter = customer.getBalance().subtract(entities.getPrice());
		payment.setAfterAmount(balanceAfter);
		payment.setDate(LocalDate.now());
		payment.setContent(String.format(accountEntryContent.getPayOrder(), entities.getCode()));
		payment.setWalletDeterminant(Determinant.builder().code(determinant.getCode())
				.name(determinant.getName()).treeCode(determinant.getTreeCode()).build());
		payment.setStatus(TransactionStatus.COMPLETED);
		payment.setFormat(customer.getFormat());
		var balanceLifeAfter = customer.getBalanceLife().add(entities.getPrice());
		SodResult<WalletTransaction> result = new SodResult<>();
		WalletTransaction rs = null;
		try {
			rs = repository.add(payment);

			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(customer.getId()).and("isDelete").is(false));
			Update update = new Update();
			update.set("balance", balanceAfter);
			update.set("balanceLife", balanceLifeAfter);
			customer = mongoTemplate.findAndModify(query, update,
					FindAndModifyOptions.options().returnNew(true), Customer.class);
			result.setResult(rs);
		} catch (Exception ex) {
			result.setError(true);
			result.setMessage(ex.getMessage());
		}
		return result;
	}

	@Override
	public SodSearchResult<WalletTransaction> findAllByWallet(String username, int pageIndex,
			int pageSize) {

		Pageable pageable =
				PageRequest.of(pageIndex, pageSize, new Sort(Direction.DESC, "createdDate"));

		Page<WalletTransaction> page =
				repository.findAllByWallet_CustomerUserName(username, pageable);

		return new SodSearchResult<>(page.getContent(), page.getTotalElements(),
				page.getTotalPages());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.WalletTransactionService#create(com.sodo.
	 * xmarketing.model.wallet.WalletTransaction, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<WalletTransaction> create(WalletTransaction entity, CurrentUser curentUser) {
		entity.setLastModifiedBy(curentUser.getCode());
		entity.setCreatedDate(LocalDateTime.now());
		entity.setCreatedBy(curentUser.getUserName());
		try {
			return SodResult.<WalletTransaction>builder().result(repository.add(entity)).build();
		} catch (Exception e) {
			return SodResult.<WalletTransaction>builder().isError(true).message(e.getMessage())
					.build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.WalletTransactionService#initData(com.sodo.
	 * xmarketing.model.account.CurrentUser)
	 */
	@Override
	public void initData(CurrentUser currentUser) throws SodException {
		initDataHelper.initdata(currentUser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.WalletTransactionService#initAccountEntryContent( )
	 */
	@Override
	public void initAccountEntryContent() {
		initDataHelper.initAccountEntryContent();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.WalletTransactionService#filterAccounting(com.
	 * sodo.xmarketing.dto .AccountingEntryFilter, int, int)
	 */
	@Override
	public SodSearchResult<WalletTransaction> filterAccounting(AccountingEntryFilter data, int page,
			int size) {
		if (!Strings.isNullOrEmpty(data.getWalletDeterminantCode())) {
			WalletDeterminant walletDeterminant =
					walletDeterminantRepo.findByCode(data.getWalletDeterminantCode());
			data.setWalletDeterminantCode(walletDeterminant.getTreeCode());
		}
		return repository.filterAccounting(data, page, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.WalletTransactionService#
	 * suggestAccountingEntryCode(java.lang. String, int)
	 */
	@Override
	public List<WalletTransaction> suggestAccountingEntryCode(String querry, int numberRecord) {
		return repository.suggestAccountingEntryCode(querry, numberRecord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.WalletTransactionService#handleBeforeCreate(com.
	 * sodo.xmarketing. model.wallet.WalletTransaction,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public WalletTransaction handleBeforeCreate(WalletTransaction accountingEntry,
			CurrentUser currentUser) {

		Customer customer =
				customerService.findByUsername(accountingEntry.getWallet().getCustomerUserName());

		var determinant =
				walletDeterminantRepo.findByCode(accountingEntry.getWalletDeterminant().getCode());
		return WalletTransaction.builder().amount(accountingEntry.getAmount())
				.beforeAmount(customer.getBalance())
				.wallet(Wallet.builder().customerCode(customer.getCode())
						.customerEmail(customer.getEmail()).customerUserName(customer.getUsername())
						.customerName(customer.getName()).build())
				.target(TargetObject.builder().code(customer.getCode())
						.type(TargetObjectType.CUSTOMER).build())
				.code(nextSequence.genWalletTransactionCode()).date(LocalDate.now())
				.type(accountingEntry.getType())
				.walletDeterminant(Determinant.builder().code(determinant.getCode())
						.name(determinant.getName()).treeCode(determinant.getTreeCode()).build())
				.status(TransactionStatus.WAITTING).format(customer.getFormat())
				.fundTransact(accountingEntry.getFundTransact())
				.content(accountingEntry.getContent()).build();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.WalletTransactionService#acceptPayment(com.sodo.
	 * xmarketing.model. wallet.WalletTransaction, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public WalletTransaction acceptPayment(WalletTransaction accountingEntry,
			CurrentUser currentUser) throws SodException {
		WalletTransaction tobeUpdateAccountingEntry =
				repository.getByCode(accountingEntry.getCode());

		// kiểm tra dữ liệu phù hợp
		checkValidRequest(tobeUpdateAccountingEntry, accountingEntry, currentUser);

		// Thực hiện nghiệp vụ với giao dịch có định khoản trừ ví

		List<Object> values = Arrays.asList(tobeUpdateAccountingEntry.getAmount(),
				tobeUpdateAccountingEntry.getContent());

		WalletTransaction finalAccounting = new WalletTransaction();
		String typeTransact = tobeUpdateAccountingEntry.getType().name();
		// Set account accept // Set time accept
		tobeUpdateAccountingEntry.setAccountantAccept(currentUser.getUserName());
		tobeUpdateAccountingEntry.setTimeAccept(LocalDateTime.now());

		// kiểm tra trạng thái của giao dịch ví nếu đã hoàn thành ko cho thực hiện tiếp
		if (!tobeUpdateAccountingEntry.getStatus().equals(TransactionStatus.COMPLETED.name())) {

			if (typeTransact.equals(TransactionType.DEBIT.name())) {
				return (updateDeposit(tobeUpdateAccountingEntry, currentUser));
			}

			if (typeTransact.equals(TransactionType.CREDIT.name())) {
				TransactionChain transactionChain = transactionChainRepository
						.getByCode(tobeUpdateAccountingEntry.getTransactionChainCode() == null ? ""
								: tobeUpdateAccountingEntry.getTransactionChainCode());

				return (updateWithDrawal(tobeUpdateAccountingEntry, currentUser, transactionChain));
			}
			// gui message and quee

		} else {
			throw new SodException(TranslateKey.RESOLVED.getReasonPhrase(),
					TranslateKey.RESOLVED.name());
		}
		return finalAccounting;
	}

	/**
	 * @param tobeUpdateAccountingEntry
	 * @param currentUser
	 * @param transactionChain
	 * @return
	 * @throws SodException
	 */
	private WalletTransaction updateWithDrawal(WalletTransaction tobeUpdateAccountingEntry,
			CurrentUser currentUser, TransactionChain transactionChain) throws SodException {

		BigDecimal amount = tobeUpdateAccountingEntry.getAmount();
		WalletTransaction finalAccounting = new WalletTransaction();

		Customer customer = customerRepository
				.findByUsername(tobeUpdateAccountingEntry.getWallet().getCustomerUserName());

		if (customer == null) {
			throw new SodException("not found customer", "ACCOUNTING");
		} else if (customer.getBalance().compareTo(amount) >= 0) {
			tobeUpdateAccountingEntry.setBeforeAmount(customer.getBalance());

			Customer rs =
					customerService.updateBalance(customer.getId(), amount, TransactionType.CREDIT);


			if (rs != null) {
				tobeUpdateAccountingEntry.setStatus(TransactionStatus.COMPLETED);
				tobeUpdateAccountingEntry.setAfterAmount(customer.getBalance().subtract(amount));
				finalAccounting = repository.update(tobeUpdateAccountingEntry);
				if (transactionChain != null && finalAccounting != null) {
					updateSuccessTransactionChain(transactionChain, tobeUpdateAccountingEntry,
							currentUser);
				}
			}
		} else {
			throw new SodException(ErrorCode.NOT_ENOUGH_MONEY.getReasonPhrase(),
					ErrorCode.NOT_ENOUGH_MONEY.name());
		}
		return finalAccounting;
	}


	private void updateSuccessTransactionChain(TransactionChain transactionChain,
			WalletTransaction tobeFundTransact, CurrentUser currentUser) {
		Map<String, Object> value = new HashMap<>();
		HistoryTransaction historyTransaction = HistoryTransaction.builder()
				.content("Accept transaction: " + tobeFundTransact.getCode())
				.createdDate(LocalDateTime.now()).createdBy(currentUser.getUserName()).build();

		value.put("historyTransaction", historyTransaction);
		value.put("currentTransaction", tobeFundTransact.getCode());
		value.put("lastModifiedDate", LocalDateTime.now());
		if (transactionChain.getAbsoluteOrder().indexOf(
				tobeFundTransact.getCode()) == transactionChain.getAbsoluteOrder().size() - 1) {
			value.put("status", TransactionChainStatus.COMPLETED.name());
		} else {
			value.put("status", TransactionChainStatus.TRADING.name());
		}
		transactionChainRepository.updateField(transactionChain.getCode(), "", true, value);
	}

	/**
	 * @param tobeUpdateAccountingEntry
	 * @param currentUser
	 * @return
	 * @throws SodException
	 */
	private WalletTransaction updateDeposit(WalletTransaction tobeUpdateAccountingEntry,
			CurrentUser currentUser) throws SodException {
		BigDecimal amount = tobeUpdateAccountingEntry.getAmount();
		WalletTransaction finalAccounting = new WalletTransaction();

		Customer customer = customerRepository
				.findByUsername(tobeUpdateAccountingEntry.getWallet().getCustomerUserName());
		if (customer == null) {
			throw new SodException("not found customer", "ACCOUNTING");
		} else {
			tobeUpdateAccountingEntry.setBeforeAmount(customer.getBalance());
			tobeUpdateAccountingEntry.setAfterAmount(customer.getBalance().add(amount));
			Customer rs =
					customerService.updateBalance(customer.getId(), amount, TransactionType.DEBIT);

			if (rs != null) {
				tobeUpdateAccountingEntry.setStatus(TransactionStatus.COMPLETED);
				finalAccounting = repository.update(tobeUpdateAccountingEntry);
			}
		}
		return finalAccounting;
	}

	void checkValidRequest(WalletTransaction tobeUpdateAccountingEntry,
			WalletTransaction accountingEntry, CurrentUser currentUser) throws SodException {
		if (tobeUpdateAccountingEntry == null) {
			throw new SodException("Not found AccountingEntry", "search");
		}
		// update trường lastModifiedDate ngay lập tức
		Map<String, Object> value = new HashMap<>();
		value.put("lastModifiedDate", LocalDateTime.now());
		repository.updateField(accountingEntry.getCode(), currentUser, "", "lastModifiedDate",
				value);
		// Không tiếp tục thực hiện giao dịch nếu giao dịch không ở trạng thái CHỜ
		if (!tobeUpdateAccountingEntry.getStatus().equals(TransactionStatus.WAITTING)) {
			throw new SodException(ErrorCode.RESOLVED.getReasonPhrase(), ErrorCode.RESOLVED.name());
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletTransactionService#update(com.sodo.xmarketing.model.wallet.WalletTransaction, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public WalletTransaction update(WalletTransaction accountingEntry, CurrentUser currentUser) throws SodException {
	    WalletDeterminant determinant =
	    		walletDeterminantRepo.findByCode(accountingEntry.getWalletDeterminant().getCode());
	        if (determinant != null) {
	          accountingEntry.setTreeCode(determinant.getTreeCode());
	        }

	        WalletTransaction tobeUpdateAccountingEntry =
	        		repository.findById(accountingEntry.getId()).orElse(null);
	        if (tobeUpdateAccountingEntry == null) {
	          throw new SodException("Accounting entry not found", "search");
	        }
	        if (!tobeUpdateAccountingEntry.getStatus().equals(TransactionStatus.WAITTING)) {
	          throw new SodException(ErrorCode.RESOLVED.getReasonPhrase(), ErrorCode.RESOLVED.name());
	        }

	        return repository.update(accountingEntry);
	}
}
