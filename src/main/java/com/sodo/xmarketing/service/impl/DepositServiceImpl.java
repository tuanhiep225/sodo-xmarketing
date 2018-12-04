/**
 * 
 */
package com.sodo.xmarketing.service.impl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.account.Deposit;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.repository.DepositRepository;
import com.sodo.xmarketing.service.DepositService;
import com.sodo.xmarketing.utils.ErrorCode;

/**
 * @author tuanhiep225
 *
 */
@Service
public class DepositServiceImpl implements DepositService {
	  @Autowired
	  DepositRepository depositRepository;

	  @Autowired
	  NextSequenceService sequenceService;

	private static final Log LOGGER = LogFactory.getLog(DepositServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#createDeposit(com.sodo.xmarketing.model.account.Deposit, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Deposit createDeposit(@Valid Deposit deposit, CurrentUser currentUser) throws SodException {
	    deposit.setCode(sequenceService.genDepositCode());
	    if(currentUser.isCustomer()) {
	    	deposit.setCustomerName(currentUser.getFullName());
	    	deposit.setUserName(currentUser.getUserName());
	    }
	    deposit.setCreatedDate(LocalDateTime.now());
	    Deposit addedDeposit = depositRepository.add(deposit);
	    return addedDeposit;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#findOne(java.lang.String)
	 */
	@Override
	public Deposit findOne(String id) {
		 return depositRepository.findById(id).orElse(null);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#updateDeposit(com.sodo.xmarketing.model.account.Deposit, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Deposit updateDeposit(@Valid Deposit deposit, CurrentUser currentUser) {

	    Deposit updatedDeposit = depositRepository.save(deposit);
	    return updatedDeposit;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#getAllDeposit(org.springframework.data.domain.PageRequest)
	 */
	@Override
	public List<Deposit> getAllDeposit(PageRequest pageRequest) {
		Page<Deposit> deposits = depositRepository.getAllDeposit(pageRequest);

	    return Lists.newArrayList(deposits);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#updateField(java.lang.String, com.sodo.xmarketing.model.account.CurrentUser, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public Deposit updateField(String code, CurrentUser currentUser, String action, String fieldName,
			Map<String, Object> value) {
		return depositRepository.updateField(code, currentUser, action, fieldName, value);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#geDepositByFilter(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.springframework.data.domain.PageRequest)
	 */
	@Override
	public Map<String, Object> geDepositByFilter(String customerAccount, String staffCode,String senderName, String cardNumber, String startDate,
			String endDate, String status, PageRequest request) throws SodException {
		return depositRepository.getDepositByFilter(customerAccount, staffCode, senderName,cardNumber, startDate,
		        endDate, status, request);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#getByCode(java.lang.String)
	 */
	@Override
	public Deposit getByCode(String code) {
		return depositRepository.getByCode(code);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.DepositService#acceptOrRefuse(java.lang.String, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Deposit acceptOrRefuse(String code, String status, CurrentUser currentUser) {
		return depositRepository.acceptOrRefuse(code, status, currentUser);
	}
}
