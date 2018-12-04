/**
 * 
 */
package com.sodo.xmarketing.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.model.HistoryPayment;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.repository.HistoryPaymentRepository;
import com.sodo.xmarketing.service.HistoryPaymentService;

/**
 * @author tuanhiep225
 *
 */
@Service
public class HistoryPaymentServiceImpl implements HistoryPaymentService {

  private static final Log LOGGER = LogFactory.getLog(HistoryPaymentServiceImpl.class);

  @Autowired
  private HistoryPaymentRepository historyPaymentRepo;

  /*
   * (non-Javadoc)
   * 
   * @see com.sodo.xmarketing.service.HistoryPaymentService#getAll(java.lang.String)
   */
  @Override
  public SodResult<Collection<HistoryPayment>> getAll(String username) {
    SodResult<Collection<HistoryPayment>> result = new SodResult<>();
    Collection<HistoryPayment> rs = null;

    try {
      rs = historyPaymentRepo.getByUsername(username);
      result.setResult(rs);
    } catch (Exception ex) {
      result.setResult(rs);
      result.setError(true);
      result.setCode(ex.getMessage());
    } ;
    return result;
  }



  /*
   * (non-Javadoc)
   * 
   * @see com.sodo.xmarketing.service.HistoryPaymentService#getAll(java.lang.String,
   * org.springframework.data.domain.Pageable)
   */
  @Override
  public SodResult<Page<HistoryPayment>> getAll(String username, Pageable page) {
    SodResult<Page<HistoryPayment>> result = new SodResult<>();
    Page<HistoryPayment> rs = null;
    try {
      rs = historyPaymentRepo.getByUsername(username, page);
      result.setResult(rs);
    } catch (Exception ex) {
      result.setError(true);
      result.setMessage(ex.getMessage());
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sodo.xmarketing.service.HistoryPaymentService#getByOrderId(java.lang.String)
   */
  @Override
  public SodResult<HistoryPayment> getByOrderId(String orderId) {
    SodResult<HistoryPayment> result = new SodResult<>();
    HistoryPayment rs = null;
    try {
      rs = historyPaymentRepo.getByOrderId(orderId);
      result.setResult(rs);
    } catch (Exception ex) {
      result.setError(true);
      result.setMessage(ex.getMessage());
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sodo.xmarketing.service.HistoryPaymentService#create(java.util.Collection)
   */
  @Override
  public SodResult<Collection<HistoryPayment>> create(Collection<HistoryPayment> entities,
      CurrentUser curentUser) {

    entities.forEach(x -> {
      x.setUsername(curentUser.getUserName());
      x.setCreatedBy(curentUser.getUserName());
      x.setCreatedDate(LocalDateTime.now());
      x.setPrice(x.getOrder().getPrice());
      x.setTotalPrice(x.getOrder().getPrice());
    });

    SodResult<Collection<HistoryPayment>> result = new SodResult<>();
    Collection<HistoryPayment> rs = null;

    try {
      rs = historyPaymentRepo.insert(entities);
      result.setResult(rs);
    } catch (Exception ex) {
      result.setResult(rs);
      result.setError(true);
      result.setCode(ex.getMessage());
    } ;
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sodo.xmarketing.service.HistoryPaymentService#create(com.sodo.xmarketing.model.
   * HistoryPayment)
   */
  @Override
  public SodResult<HistoryPayment> create(HistoryPayment entities, CurrentUser curentUser) {
    // TODO Auto-generated method stub
    SodResult<HistoryPayment> result = new SodResult<>();
    HistoryPayment rs = null;
    try {
      rs = historyPaymentRepo.add(entities);
      result.setResult(rs);
    } catch (Exception ex) {
      result.setError(true);
      result.setMessage(ex.getMessage());
    }
    return result;
  }

  @Override
  public SodResult<Collection<HistoryPayment>> createFromOrder(Collection<Order> entities,
      CurrentUser curentUser) {
    Collection<HistoryPayment> models = new ArrayList<HistoryPayment>();
    entities.forEach(x -> {
      HistoryPayment historyPayment = new HistoryPayment();
      historyPayment.setOrder(x);
      historyPayment.setUsername(curentUser.getUserName());
      historyPayment.setCreatedBy(curentUser.getUserName());
      historyPayment.setCreatedDate(LocalDateTime.now());
      historyPayment.setPrice(x.getPrice());
      historyPayment.setTotalPrice(x.getPrice());
      models.add(historyPayment);
    });

    SodResult<Collection<HistoryPayment>> result = new SodResult<>();
    Collection<HistoryPayment> rs = null;

    try {
      rs = historyPaymentRepo.insert(models);
      result.setResult(rs);
    } catch (Exception ex) {
      result.setResult(rs);
      result.setError(true);
      result.setCode(ex.getMessage());
    } ;
    return result;
  }

  @Override
  public SodResult<HistoryPayment> createFromOrder(Order entities, CurrentUser curentUser) {
    HistoryPayment payment = new HistoryPayment();
    payment.setOrder(entities);
    payment.setUsername(curentUser.getUserName());
    payment.setCreatedBy(curentUser.getUserName());
    payment.setCreatedDate(LocalDateTime.now());
    payment.setPrice(entities.getPrice());

    SodResult<HistoryPayment> result = new SodResult<>();
    HistoryPayment rs = null;
    try {
      rs = historyPaymentRepo.add(payment);
      result.setResult(rs);
    } catch (Exception ex) {
      result.setError(true);
      result.setMessage(ex.getMessage());
    }
    return result;

  }

}
