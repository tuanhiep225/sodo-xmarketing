/**
 * 
 */
package com.sodo.xmarketing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.FundInfo;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.HistoryTransaction;
import com.sodo.xmarketing.model.TransactionChain;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.fund.Fund;
import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.repository.CustomerRepository;
import com.sodo.xmarketing.repository.TransactionChainRepository;
import com.sodo.xmarketing.repository.fund.FundDeterminantRepository;
import com.sodo.xmarketing.repository.fund.FundRepository;
import com.sodo.xmarketing.repository.fund.FundTransactRepository;
import com.sodo.xmarketing.repository.wallet.WalletDeterminantRepository;
import com.sodo.xmarketing.repository.wallet.WalletTransactionRepository;
import com.sodo.xmarketing.service.FundTransactService;
import com.sodo.xmarketing.service.WalletTransactionService;
import com.sodo.xmarketing.utils.ConfigHelper;
import com.sodo.xmarketing.utils.ErrorCode;
import com.sodo.xmarketing.utils.FundTransactHelper;
import com.sodo.xmarketing.utils.Properties;
import com.sodo.xmarketing.utils.StringUtils;
import com.sodo.xmarketing.utils.SystemFunDeter;
import com.sodo.xmarketing.utils.TransactionChainStatus;

/**
 * @author tuanhiep225
 *
 */
@Service
public class FundTransactServiceImpl implements FundTransactService {

	@Autowired
	NextSequenceService sequenceService;

	@Autowired
	private FundTransactRepository fundTransactRepository;

	@Autowired
	private FundDeterminantRepository fundDeterminantRepository;

	@Autowired
	FundRepository fundRepository;

	@Autowired
	private ConfigHelper configHelper;

	@Autowired
	private Properties properties;

	@Autowired
	private WalletDeterminantRepository walletDeterminantRepository;

	@Autowired
	private WalletTransactionRepository accountingEntryRepository;

	@Autowired
	private WalletTransactionService accountingEntryService;

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	FundTransactHelper fundTransactHelper;
	
	  @Autowired
	  TransactionChainRepository transactionChainRepository;

	private static final Log LOGGER = LogFactory.getLog(FundTransactServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundTransactService#create(com.sodo.xmarketing.model.fund.
	 * FundTransaction, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public FundTransaction create(FundTransaction fundTransaction, CurrentUser currentUser) {

		if (fundTransaction.getTransferFee() == null) {
			fundTransaction.setTransferFee(BigDecimal.ZERO);
		}
		fundTransaction.setCode(sequenceService.genFundTransactCode());
		fundTransaction.setTextSearch(fundTransaction.getTextSearch()
				+ StringUtils.unAccent(fundTransaction.getCode()).toLowerCase());
		fundTransaction.setEmployeeCreate(currentUser.getUserName());
		FundDeterminant determinant =
				fundDeterminantRepository.findByCode(fundTransaction.getDeterminant().getCode());
		if (determinant != null) {
			fundTransaction.setDeterminant(Determinant.builder().code(determinant.getCode())
					.name(determinant.getName()).treeCode(determinant.getTreeCode()).build());
			fundTransaction.setTreeCode(determinant.getTreeCode());
		}
		// Nếu không có ngày chứng từ thì lấy bằng ngày tạo luôn
		if (fundTransaction.getTimeReport() == null) {
			fundTransaction.setTimeReport(LocalDate.now());
		}
		
		Fund fund = fundRepository.getByCode(fundTransaction.getFund().getCode());
		
		FundInfo fundInfo = fundTransaction.getFund();
		fundInfo.setName(fund.getName());
		
		fundTransaction.setCreatedDate(LocalDateTime.now());
		fundTransaction.setLastModifiedDate(LocalDateTime.now());
		
	    // Nếu Có định khoản chuyển quỹ thì tạo kèm các giao dịch đi kèm
	    if (SystemFunDeter.TRANSFER_FUND.contains(fundTransaction.getDeterminant().getCode())) {
	      // Sinh mã các giao dịch
	      String subTransactCode = fundTransaction.getCode();
	      String addTransactCode = sequenceService.genFundTransactCode();
	      List<FundTransaction> transaction = new ArrayList();

	      TransactionChain transactionChain = new TransactionChain();
	      transactionChain.setCode(sequenceService.genTransactionChaninCode());
	      transactionChain.setSender(fundTransaction.getFund().getCode());
	      transactionChain.setReceiver(fundTransaction.getTargetFundCode());
	      transactionChain.setStatus(TransactionChainStatus.CREATED.name());
	      transactionChain.setHistoryTransaction(Arrays.asList(
	          new HistoryTransaction("Tạo mới", LocalDateTime.now(), currentUser.getUserName())));
	      transactionChain.setAbsoluteOrder(new ArrayList());
	      transactionChain.setInputTransaction(new ArrayList());
	      transactionChain.setOutputTransaction(new ArrayList());
	      transactionChain.getInputTransaction().add(subTransactCode);
	      transactionChain.getAbsoluteOrder().add(subTransactCode);
	      fundTransaction.setTransactionChainCode(transactionChain.getCode());
	      transaction.add(fundTransaction);

	      // Mã giao dịch Cộng quỹ đích
	      transactionChain.getAbsoluteOrder().add(addTransactCode);
	      transactionChain.getOutputTransaction().add(addTransactCode);
	      transaction.add(fundTransactHelper.createAddTransact(addTransactCode, fundTransaction));
	      // Create transaction Chain
	      transactionChainRepository.add(transactionChain);
	      return fundTransactRepository.insert(transaction).get(0);
	    } else {
	    	return fundTransactRepository.add(fundTransaction);
	    }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundTransactService#getByCode(java.lang.String)
	 */
	@Override
	public FundTransaction getByCode(String code) {
		return fundTransactRepository.getByCode(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundTransactService#filterFundTransact(java.lang.String,
	 * java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * org.springframework.data.domain.Pageable)
	 */
	@Override
	public Map<String, Object> filterFundTransact(String timeReportFrom, String timeReportTo,
			Boolean absoluteDeter, String code, String objectType, String objectCode,
			String fundCode, String transactionType, String determinantEntryCode, String currency,
			String enabled, String amountFrom, String amountTo, String createdDateFrom,
			String createdDateTo, PageRequest request) throws SodException {
		if (!Strings.isNullOrEmpty(determinantEntryCode)) {
			FundDeterminant determinant =
					fundDeterminantRepository.findByCode(determinantEntryCode);
			return fundTransactRepository.filterFundTransact(timeReportFrom, timeReportTo,
					absoluteDeter, code, objectType, objectCode, fundCode, transactionType,
					determinant.getTreeCode(), currency, enabled, amountFrom, amountTo,
					createdDateFrom, createdDateTo, request);
		}
		return fundTransactRepository.filterFundTransact(timeReportFrom, timeReportTo,
				absoluteDeter, code, objectType, objectCode, fundCode, transactionType,
				determinantEntryCode, currency, enabled, amountFrom, amountTo, createdDateFrom,
				createdDateTo, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundTransactService#suggestFundTransact(java.lang.String,
	 * int)
	 */
	@Override
	public List<FundTransaction> suggestFundTransact(String query, int numberRecord)
			throws SodException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.FundTransactService#acceptPayment(com.sodo.xmarketing.model.fund.
	 * FundTransaction, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<FundTransaction> acceptPayment(FundTransaction FundTransaction,
			CurrentUser currentUser) throws SodException {

		FundTransaction tobeFundTransact =
				fundTransactRepository.getByCode(FundTransaction.getCode());

		if (tobeFundTransact == null) {
			throw new SodException(
					"Không tìm thấy phiếu giao dịch quỹ, FundTransaction: " + FundTransaction,
					ErrorCode.NOT_FOUND_ACCOUNTING_ENTRY.name());
		}

//		if (!FunctionalUtils.isExpried(tobeFundTransact.getLastModifiedDate(),
//				FundTransaction.getLastModifiedDate())) {
//			throw new SodException(ErrorCode.EXPIRED_DATE_UPDATE.getReasonPhrase(),
//					ErrorCode.EXPIRED_DATE_UPDATE.name());
//		}

		tobeFundTransact.setTimeAccept(LocalDateTime.now());
		tobeFundTransact.setAccountantAccept(currentUser.getUserName());

		// Giao dịch quỹ đã hoàn thành
		if (tobeFundTransact.getStatus().equals(TransactionStatus.COMPLETED.toString())) {
			throw new SodException(ErrorCode.RESOLVED.getReasonPhrase(), ErrorCode.RESOLVED.name());
		}

	    // Killer: Kiểm tra giao dịch nếu thuộc loại giao dịch chuỗi
	    TransactionChain transactionChain =
	        transactionChainRepository.getByCode(tobeFundTransact.getTransactionChainCode() == null ? ""
	            : tobeFundTransact.getTransactionChainCode());
	    if (transactionChain != null) {
	      String currentTransact = transactionChain.getCurrentTransaction();
	      List<String> absoluteOrder = transactionChain.getAbsoluteOrder();
	      // nếu chưa có giao dịch nào và giao dịch đang thực hiện là bắt đầu của chuỗi giao dịch thì
	      // cho phép thực hiện
	      if ((Strings.isNullOrEmpty(currentTransact) && absoluteOrder.indexOf(tobeFundTransact.getCode()) == 0)
	          || (!Strings.isNullOrEmpty(currentTransact) && absoluteOrder
	              .indexOf(tobeFundTransact.getCode()) == absoluteOrder.indexOf(currentTransact) + 1)
	          || absoluteOrder.indexOf(tobeFundTransact.getCode()) == absoluteOrder.size()) {

	      } else {
	        SodResult<FundTransaction> resultData = new SodResult<>();
	        resultData.setError(true);
	        resultData.setMessage(absoluteOrder.get(absoluteOrder.indexOf(currentTransact) + 1));
	        resultData.setCode(ErrorCode.WRONG_ORDER_TRANSACTION.name());
	        return resultData;
	      }
	    }
		if (TransactionType.DEBIT.name().equals(tobeFundTransact.getTransactionType())) {
			return updateDeposit(tobeFundTransact, currentUser,transactionChain);
		} else if (TransactionType.CREDIT.name().equals(tobeFundTransact.getTransactionType())) {
			return updateWithDrawal(tobeFundTransact, currentUser,transactionChain);
		} else {
			SodResult<FundTransaction> result = new SodResult<>();
			result.setError(true);
			result.setCode("TRANSACTION_TYPE");
			result.setMessage("Giao dịch không có loại giao dịch");
			return result;
		}
	}

	SodResult<FundTransaction> updateDeposit(FundTransaction tobeFundTransact,
			CurrentUser currentUser, TransactionChain transactionChain) throws SodException {
		SodResult<FundTransaction> result = new SodResult<>();

		Map<String, Object> mapFundTransactField = new HashMap<>();
		Map<String, Object> mapFundField = new HashMap<>();

		Fund fund = fundRepository.getByCode(tobeFundTransact.getFund().getCode());
		if (fund == null) {
			throw new SodException("not found fund", ErrorCode.NOT_FOUND_FUND.name());
		}

		// set balance before
		tobeFundTransact.setBalanceBefore(fund.getBalance());
		tobeFundTransact.setStatus(TransactionStatus.COMPLETED.name());
		fund.setBalance(fund.getBalance().add(tobeFundTransact.getAmount()));

		// set balance after
		tobeFundTransact.setBalanceAfter(fund.getBalance());

		// Cập nhật balance cho quỹ
		mapFundField.put("balance", fund.getBalance());

		boolean checkUpdateFund =
				fundRepository.updateFieldsFund(fund.getCode(), mapFundField, currentUser);

		if (!checkUpdateFund) {
			result.setError(true);
			result.setCode("UPDATE_FUND");
			result.setMessage("Cập nhật quỹ thất bại");
			return result;
		}

		mapFundTransactField.put("balanceBefore", tobeFundTransact.getBalanceBefore());
		mapFundTransactField.put("balanceAfter", tobeFundTransact.getBalanceAfter());
		mapFundTransactField.put("status", tobeFundTransact.getStatus());
		mapFundTransactField.put("accountantAccept", tobeFundTransact.getAccountantAccept());

		boolean checkUpdateFundTransact = fundTransactRepository.updateFieldsFundTransact(
				tobeFundTransact.getCode(), mapFundTransactField, currentUser);

	    if (!checkUpdateFundTransact) {
	        result.setError(true);
	        result.setCode("UPDATE_FUNDTRANSACT");
	        result.setMessage("Cập nhật giao dịch quỹ thất bại");
	        return result;
	      } else {
	        // Killer cập nhật lịch sử chuỗi giao dịch nếu thực hiện thành công
	        if (transactionChain != null) {
	          updateSuccessTransactionChain(transactionChain, tobeFundTransact, currentUser);
	        }
	      }
		result.setError(false);
		result.setCode("SUCCESS");
		result.setMessage("Thành công");

		return result;
	}

	SodResult<FundTransaction> updateWithDrawal(FundTransaction tobeFundTransact,
			CurrentUser currentUser, TransactionChain transactionChain) throws SodException {
		SodResult<FundTransaction> result = new SodResult<>();

		Map<String, Object> mapFundTransactField = new HashMap<>();
		Map<String, Object> mapFundField = new HashMap<>();

		BigDecimal amount = tobeFundTransact.getAmount();

		Fund fund = fundRepository.getByCode(tobeFundTransact.getFund().getCode());

		if (fund == null) {
			throw new SodException("not fund customer", ErrorCode.NOT_FOUND_FUND.name());
		}


		// set balance before
		tobeFundTransact.setStatus(TransactionStatus.COMPLETED.name());
		tobeFundTransact.setBalanceBefore(fund.getBalance());

		fund.setBalance(fund.getBalance().subtract(amount));

		// set balance after
		tobeFundTransact.setBalanceAfter(fund.getBalance());

		// Cập nhật balance cho quỹ
		mapFundField.put("balance", fund.getBalance());
		boolean checkUpdateFund =
				fundRepository.updateFieldsFund(fund.getCode(), mapFundField, currentUser);

		if (!checkUpdateFund) {
			result.setError(true);
			result.setCode("UPDATE_FUND");
			result.setMessage("Cập nhật quỹ thất bại");
			return result;
		}

		// Cập nhật giao dịch quỹ
		mapFundTransactField.put("balanceBefore", tobeFundTransact.getBalanceBefore());
		mapFundTransactField.put("balanceAfter", tobeFundTransact.getBalanceAfter());
		mapFundTransactField.put("status", tobeFundTransact.getStatus());
		mapFundTransactField.put("accountantAccept", tobeFundTransact.getAccountantAccept());
		boolean checkUpdateFundTransact = fundTransactRepository.updateFieldsFundTransact(
				tobeFundTransact.getCode(), mapFundTransactField, currentUser);
		
	    if (!checkUpdateFundTransact) {
	        result.setError(true);
	        result.setCode("UPDATE_FUNDTRANSACT");
	        result.setMessage("Cập nhật giao dịch quỹ thất bại");
	        return result;
	      } else {
	        // Killer cập nhật lịch sử chuỗi giao dịch nếu thực hiện thành công
	        if (transactionChain != null) {
	          updateSuccessTransactionChain(transactionChain, tobeFundTransact, currentUser);
	        }
	      }
		result.setError(false);
		result.setCode("SUCCESS");
		result.setMessage("Thành công");

		return result;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.FundTransactService#updateField(java.lang.String, com.sodo.xmarketing.model.account.CurrentUser, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public FundTransaction updateField(String code, CurrentUser currentUser, String action, String fieldName,
			Map<String, Object> value) {
		return fundTransactRepository.updateField(code, currentUser, action, fieldName, value);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.FundTransactService#update(com.sodo.xmarketing.model.fund.FundTransaction)
	 */
	@Override
	public FundTransaction update(FundTransaction fundTransact) throws SodException {

	    // Nếu không có ngày chứng từ thì lấy bằng ngày tạo luôn
	    if (fundTransact.getTimeReport() == null) {
	      fundTransact.setTimeReport(fundTransact.getCreatedDate().toLocalDate());
	    }
	    FundDeterminant determinant =
	        fundDeterminantRepository.findByCode(fundTransact.getDeterminant().getCode());
	    if (determinant != null) {
	      fundTransact.setTreeCode(determinant.getTreeCode());
	    }
	    
	    // Nếu thay đổi các thông tin mà ko phải là hủy hoặc xóa của 1 phiếu giao dịch liên kết
	    if (!fundTransact.getStatus().equals(TransactionStatus.CANCEL.name())
	        && !fundTransact.getIsDelete() && fundTransact.getTransactionChainCode() != null) {
	      throw new SodException("Không thể update thông tin của loại giao dịch liên kết",
	          ErrorCode.TRANSACTION_CHAIN_CANNOT_CANCEL.name());
	    }
	    // Nếu hủy hoặc xóa phiếu giao dịch liên kết
	    if ((fundTransact.getStatus().equals(TransactionStatus.CANCEL.name())
	        || fundTransact.getIsDelete()) && fundTransact.getTransactionChainCode() != null) {
	      TransactionChain transactionChain =
	          transactionChainRepository.getByCode(fundTransact.getTransactionChainCode() == null ? ""
	              : fundTransact.getTransactionChainCode());
	      if (!transactionChain.getStatus().equals(TransactionChainStatus.CREATED.name())) {
	        throw new SodException("Không thể hủy giao dịch trong chuỗi khi đang tiến hành",
	            ErrorCode.TRANSACTION_CHAIN_CANNOT_CANCEL.name());
	      } else {
	        // Hủy hết hoặc xóa hết các giao dịch và hủy transaction chain
	        if (fundTransact.getStatus().equals(TransactionStatus.CANCEL.name())) {
	          fundTransactRepository.deleteOrCancelTransactions("cancel",
	              transactionChain.getAbsoluteOrder(), null);
	        }
	        if (fundTransact.getIsDelete()) {
	          fundTransactRepository.deleteOrCancelTransactions("delete",
	              transactionChain.getAbsoluteOrder(), null);
	        }
	        return fundTransactRepository.getByFieldName("code", fundTransact.getCode());
	      }
	    }
	    return fundTransactRepository.update(fundTransact);
	}
	
	  private void updateSuccessTransactionChain(TransactionChain transactionChain,
		      FundTransaction tobeFundTransact, CurrentUser currentUser) {
		    Map<String, Object> value = new HashMap();
		    HistoryTransaction historyTransaction =
		        new HistoryTransaction("Accept transaction: " + tobeFundTransact.getCode(),
		            LocalDateTime.now(), currentUser.getUserName());

		    value.put("historyTransaction", historyTransaction);
		    value.put("currentTransaction", tobeFundTransact.getCode());
		    value.put("lastModifiedDate", LocalDateTime.now());
		    if (transactionChain.getAbsoluteOrder()
		        .indexOf(tobeFundTransact.getCode()) == transactionChain.getAbsoluteOrder().size() - 1) {
		      value.put("status", TransactionChainStatus.COMPLETED.name());
		    } else {
		      value.put("status", TransactionChainStatus.TRADING.name());
		    }
		    transactionChainRepository.updateField(transactionChain.getCode(), "", true, value);
		  }
}
