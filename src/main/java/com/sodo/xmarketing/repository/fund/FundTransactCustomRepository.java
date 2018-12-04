/**
 * 
 */
package com.sodo.xmarketing.repository.fund;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.fund.FundTransaction;

/**
 * @author tuanhiep225
 *
 */
public interface FundTransactCustomRepository {
	/**
	 * @param code
	 * @param mapFundTransactField
	 * @param currentUser
	 * @return
	 */
	boolean updateFieldsFundTransact(String code, Map<String, Object> mapFundTransactField, CurrentUser currentUser);

	Map<String, Object> filterFundTransact(String timeReportFroim, String timeReportTo, Boolean absoluteDeter,
			String code, String objectType, String objectCode, String fundCode, String transactionType,
			String determinantEntryCode, String currency, String enabled, String amountFrom, String amountTo,
			String createdDateFrom, String createdDateTo, PageRequest request) throws SodException;

	FundTransaction updateField(String code, CurrentUser currentUser, String action, String fieldName,
			Map<String, Object> value);
	
	  void deleteOrCancelTransactions(String action, List<String> values, CurrentUser currentUser);
	  
	  FundTransaction getByFieldName(String fieldName, String value);
}
