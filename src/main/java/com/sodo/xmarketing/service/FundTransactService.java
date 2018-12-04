/**
 * 
 */
package com.sodo.xmarketing.service;

import java.util.List;
import java.util.Map;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.fund.FundTransaction;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author tuanhiep225
 *
 */
public interface FundTransactService {

    FundTransaction create(FundTransaction FundTransaction, CurrentUser currentUser);

    FundTransaction getByCode(String code);

    Map<String, Object> filterFundTransact(String timeReportFrom, String timeReportTo,
            Boolean absoluteDeter, String code, String objectType, String objectCode,
            String fundCode, String transactionType, String determinantEntryCode, String currency,
            String enabled, String amountFrom, String amountTo, String createdDateFrom,
            String createdDateTo, PageRequest request) throws SodException;

    List<FundTransaction> suggestFundTransact(String query, int numberRecord) throws SodException;

    SodResult<FundTransaction> acceptPayment(FundTransaction fundTransact, CurrentUser currentUser)
            throws SodException;

    FundTransaction updateField(String code, CurrentUser currentUser, String action, String fieldName,
            Map<String, Object> value);

	/**
	 * @param fundTransact
	 * @return
	 */
	FundTransaction update(FundTransaction fundTransact) throws SodException;

}
