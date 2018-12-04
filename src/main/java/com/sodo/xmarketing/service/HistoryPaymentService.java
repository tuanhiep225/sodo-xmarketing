/**
 * 
 */
package com.sodo.xmarketing.service;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sodo.xmarketing.model.HistoryPayment;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;

/**
 * @author tuanhiep225
 *
 */
public interface HistoryPaymentService {
  SodResult<Collection<HistoryPayment>> getAll(String username);

  SodResult<Page<HistoryPayment>> getAll(String username, Pageable page);

  SodResult<HistoryPayment> getByOrderId(String orderId);

  SodResult<Collection<HistoryPayment>> create(Collection<HistoryPayment> entities,
      CurrentUser curentUser);

  SodResult<HistoryPayment> create(HistoryPayment entities, CurrentUser curentUser);

  SodResult<Collection<HistoryPayment>> createFromOrder(Collection<Order> entities,
      CurrentUser curentUser);

  SodResult<HistoryPayment> createFromOrder(Order entities, CurrentUser curentUser);

}
