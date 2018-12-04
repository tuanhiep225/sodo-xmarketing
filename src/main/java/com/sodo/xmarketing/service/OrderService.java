/**
 * 
 */
package com.sodo.xmarketing.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sodo.xmarketing.dto.OrderDTO;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.OrderExcel;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.agency.OrderAgencyData;
import com.sodo.xmarketing.model.agency.OrderLiveStreamModel;
import com.sodo.xmarketing.model.agency.OrderModel;
import com.sodo.xmarketing.model.agency.UserModel;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;

/**
 * @author tuanhiep225
 *
 */
public interface OrderService {

  SodResult<Order> create(Order entity, CurrentUser currentUser);

  SodResult<Order> getById(String id);

  SodResult<Order> update(OrderDTO order, String code, CurrentUser currentUser);

  SodResult<Page<Order>> get(int page, int pageSize);

  SodResult<Collection<Order>> creates(Collection<Order> entities, CurrentUser currentUser);

  SodResult<Collection<Order>> getAll();

  Map<String, Long> groupByStatusAndCount(String username);
  
  SodSearchResult<Order> filter(String param, String keyword, PageRequest page,
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
SodSearchResult<Order> filterForMyOrder(String param, String keyword, PageRequest pageable, CurrentUser currentUser);

/**
 * @param code
 * @return
 */
SodResult<Order> getByCode(String code);

/**
 * @param entity
 * @param currentUser
 * @return
 */
SodResult<Boolean> refund(String code, OrderDTO entity, CurrentUser currentUser);

/**
 * @param currentUser
 * @return
 */
Boolean checkOrderFree(CurrentUser currentUser);

/**
 * @param entities
 * @param currentUser
 * @return
 */
SodResult<Order> createOrderTrial(Order entity, CurrentUser currentUser);

/**
 * @param isToday
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
 * @param entity
 * @return
 * @throws SodException 
 */
SodResult<String> createForAgency(OrderModel entity) throws SodException;

/**
 * @param entity
 * @param orderCode
 * @return
 */
SodResult<OrderAgencyData> getOneForAgency(UserModel entity, String orderCode);

/**
 * @param entity
 * @return
 */
SodResult<Collection<OrderAgencyData>> getAllForAgency(UserModel entity);

/**
 * @param param
 * @param keyword
 * @param pageable
 * @param currentUser
 * @param role
 * @return
 */
SodSearchResult<Order> filterV2(String param, String keyword, PageRequest pageable, CurrentUser currentUser,
		String role);

/**
 * @param orderSearch
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Order> filterV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);

/**
 * @param orderSearch
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Order> filterReceiveV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);

/**
 * @param orderSearch
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Order> filterForMyOrderV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);

/**
 * @param orderSearch
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<Order> filterOrderForSales(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);

/**
 * @param entity
 * @return
 * @throws SodException 
 */
SodResult<String> createOrderLiveStreamForAgency(OrderLiveStreamModel entity) throws SodException;

/**
 * @param orderSearch
 * @param pageable
 * @param currentUser
 * @return
 */
SodSearchResult<OrderExcel> filterV3ForExcel(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser);

}
