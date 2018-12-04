/**
 * 
 */
package com.sodo.xmarketing.service;

import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.dto.OrderDistributorSearch;
import com.sodo.xmarketing.model.OrderDistributor;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.wallet.TransactionStatus;

/**
 * @author tuanhiep225
 *
 */
public interface OrderDistributorService {

	/**
	 * @param orderDistributor
	 * @param currentUser
	 * @return
	 */
	OrderDistributor create(OrderDistributor orderDistributor, CurrentUser currentUser);

	/**
	 * @param orderCode
	 * @return
	 */
	OrderDistributor getByOrderCode(String orderCode);

	/**
	 * @param code
	 * @return
	 */
	OrderDistributor requestPayment(String code);

	/**
	 * @param orderDistributor
	 * @param currentUser
	 * @return
	 */
	SodResult<OrderDistributor> update(OrderDistributor orderDistributor, CurrentUser currentUser);

	/**
	 * @param orderDistributorSearch
	 * @param pageable 
	 * @param currentUser
	 * @return
	 */
	SodSearchResult<OrderDistributor> filter(OrderDistributorSearch orderDistributorSearch, PageRequest pageable, CurrentUser currentUser);

	/**
	 * @param code
	 * @return
	 */
	OrderDistributor findByCode(String code);

	/**
	 * @param code
	 * @param currentUser
	 * @return
	 */
	SodResult<OrderDistributor> updateTransactionStatus(String code, TransactionStatus status, CurrentUser currentUser);
	
}
