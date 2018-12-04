/**
 * 
 */
package com.sodo.xmarketing.repository;
import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.dto.OrderDistributorSearch;
import com.sodo.xmarketing.model.OrderDistributor;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;

/**
 * @author tuanhiep225
 *
 */
public interface OrderDistributorCustomRepository {

	SodSearchResult<OrderDistributor> filter(OrderDistributorSearch orderSearch, PageRequest pageable, CurrentUser currentUser);
}
