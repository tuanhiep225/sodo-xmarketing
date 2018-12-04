/**
 * 
 */
package com.sodo.xmarketing.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sodo.xmarketing.dto.OrderDTO;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.OrderExcel;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.status.OrderStatus;

/**
 * @author tuanhiep225
 *
 */
public interface OrderCustomRepository {
  public Map<String, Long> groupByStatusAndCount(String username);

  public SodSearchResult<Order> filter(String param, String keyword, PageRequest page,
      CurrentUser currentUser);
  

	/**
	 * @param param
	 * @param keyword
	 * @param pageable
	 * @param currentUser
	 * @return
	 */
	SodSearchResult<Order> filterForCMS(String param, String keyword, PageRequest pageable, CurrentUser currentUser);
	
	

	/**
	 * @param entities
	 * @param currentUser
	 * @return
	 */
	SodResult<Boolean> receive(Collection<Order> entities, CurrentUser currentUser);
	

	/**
	 * @param param
	 * @param keyword
	 * @param pageable
	 * @param currentUser
	 * @return
	 */
	SodSearchResult<Order> filterForMyOrder(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser);
	
	/**
	 * @param order
	 * @param code
	 * @param currentUser
	 * @return
	 */
	SodResult<Order> update(OrderDTO order, String code, CurrentUser currentUser);
	
	/**
	 * @param entity
	 * @param currentUser
	 * @return
	 */
	SodResult<Boolean> refund(String code, OrderDTO entity, CurrentUser currentUser);
	

	/**
	 * @param orders
	 * @return
	 */
	Boolean updateStatus(Collection<Order> orders, OrderStatus status);

	
	/**
	 * @param isToday
	 * @param role 
	 * @param currentUser
	 * @return
	 */
	Map<String, Long> cmsCountByStatus(Boolean isToday, String role, CurrentUser currentUser);
	
	/**
	 * @param dateTime
	 * @param role
	 * @param currentUser
	 * @return
	 */
	Map<String, BigDecimal> cmsTurnover(LocalDate dateTime, String role, CurrentUser currentUser);
	
	
	/**
	 * @param param
	 * @param keyword
	 * @param pageable
	 * @param currentUser
	 * @return
	 */
	SodSearchResult<Order> filterV2(String param, String keyword, PageRequest pageable, CurrentUser currentUser, String role);
	
	

	/**
	 * @param orderSearch
	 * @param pageable
	 * @param currentUser
	 * @return
	 */
	SodSearchResult<Order> filterV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);
	
	SodSearchResult<Order> filterReceiveV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);
	
	SodSearchResult<Order> filterForMyOrderV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);
	

	/**
	 * @param orderSearch
	 * @param pageable
	 * @param currentUser
	 * @return
	 */
	SodSearchResult<Order> filterOrderForSales(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);
	
	
	SodSearchResult<OrderExcel> filterV3ForExcel(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);



}
