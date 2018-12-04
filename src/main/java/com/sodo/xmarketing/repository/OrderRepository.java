/**
 * 
 */
package com.sodo.xmarketing.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.dto.OrderDTO;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;

/**
 * @author tuanhiep225
 *
 */

@Repository
public interface OrderRepository extends BaseRepository<Order, String>, OrderCustomRepository {

	/**
	 * @param code 
	 * @return
	 */
	@Query("{'isDelete':false, 'code':'?0'}")
	Order getByCode(String code);

	/**
	 * @param userName
	 * @return
	 */
	Integer countByUsernameAndIsDeleteIsFalse(String userName);
	
	
	Collection<Order> findByUsernameAndIsDeleteIsFalse(String userName);

}
