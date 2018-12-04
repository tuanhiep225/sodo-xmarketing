/**
 * 
 */
package com.sodo.xmarketing.service;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.account.Deposit;

/**
 * @author tuanhiep225
 *
 */
public interface DepositService {

	/**
	 * @param deposit
	 * @param currentUser
	 * @return
	 * @throws SodException 
	 */
	Deposit createDeposit(@Valid Deposit deposit, CurrentUser currentUser) throws SodException;

	/**
	 * @param id
	 * @return
	 */
	Deposit findOne(String id);

	/**
	 * @param deposit
	 * @param currentUser
	 * @return
	 */
	Deposit updateDeposit(@Valid Deposit deposit, CurrentUser currentUser);

	/**
	 * @param pageRequest
	 * @return
	 */
	List<Deposit> getAllDeposit(PageRequest pageRequest);

	/**
	 * @param code
	 * @param currentUser
	 * @param string
	 * @param string2
	 * @param value
	 * @return 
	 */
	Deposit updateField(String code, CurrentUser currentUser, String string, String string2, Map<String, Object> value);

	/**
	 * @param customerAccount
	 * @param employeeAccount
	 * @param startDate
	 * @param endDate
	 * @param status
	 * @param cardNumber 
	 * @param sẻ 
	 * @param request
	 * @return
	 * @throws SodException 
	 */
	Map<String, Object> geDepositByFilter(String customerAccount, String employeeAccount, String senderName, String cardNumber, String startDate,
			String endDate, String status, PageRequest request) throws SodException;

	/**
	 * @param code
	 * @return
	 */
	Deposit getByCode(String code);
	
	
	Deposit acceptOrRefuse(String code, String status, CurrentUser currentUser);
	
}
