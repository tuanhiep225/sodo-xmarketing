/**
 * 
 */
package com.sodo.xmarketing.repository;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Pageable;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.account.Deposit;

/**
 * @author tuanhiep225
 *
 */
public interface DepositCustomRepository {
	Map<String, Object> getDepositByFilter(String customerAccount, String employeeAccount, String senderName,
			String cardNumber, String startDate, String endDate, String status, Pageable pageable) throws SodException;

	Deposit updateField(String code, CurrentUser currentUser, String action, String fieldName,
			Map<String, Object> value);

	/**
	 * @param code
	 * @param status
	 * @param currentUser
	 * @return
	 */
	Deposit acceptOrRefuse(String code, String status, CurrentUser currentUser);
}
