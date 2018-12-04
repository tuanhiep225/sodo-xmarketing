/**
 * 
 */
package com.sodo.xmarketing.service.impl;
import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.dto.OrderDistributorSearch;
import com.sodo.xmarketing.model.OrderDistributor;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.repository.OrderDistributorRepository;
import com.sodo.xmarketing.service.OrderDistributorService;
import com.sodo.xmarketing.status.OrderDistributorStatus;

/**
 * @author tuanhiep225
 *
 */
@Service
public class OrderDistributorServiceImpl implements OrderDistributorService {

	@Autowired
	private OrderDistributorRepository repository;
	
	@Autowired
	private NextSequenceService nextSequenceService;
	
	private static final Log LOGGER = LogFactory.getLog(OrderDistributorServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderDistributorService#create(com.sodo.xmarketing.model.OrderDistributor, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public OrderDistributor create(OrderDistributor orderDistributor, CurrentUser currentUser) {
		orderDistributor.setCode(nextSequenceService.genOrderDistributorCode());
		orderDistributor.setStatus(OrderDistributorStatus.NEW);
		orderDistributor.setCreatedBy(currentUser.getUserName());
		orderDistributor.setCreatedDate(LocalDateTime.now());
		orderDistributor.setTransactionStatus(TransactionStatus.WAITTING);
		orderDistributor.setIsRequestPayment(false);
		return repository.add(orderDistributor);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderDistributorService#getByOrderCode(java.lang.String)
	 */
	@Override
	public OrderDistributor getByOrderCode(String orderCode) {
		return repository.getByOrderCode(orderCode);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderDistributorService#requestPayment(java.lang.String)
	 */
	@Override
	public OrderDistributor requestPayment(String code) {
		OrderDistributor entity =  repository.getByCode(code);
		if(entity == null)
			return null;
		entity.setIsRequestPayment(true);
		entity.setLastModifiedDate(LocalDateTime.now());
		entity.setStatus(OrderDistributorStatus.WAITTING);
		return repository.update(entity);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderDistributorService#update(com.sodo.xmarketing.model.OrderDistributor, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<OrderDistributor> update(OrderDistributor orderDistributor, CurrentUser currentUser) {
		OrderDistributor entity = repository.getByCode(orderDistributor.getCode());
		if(entity == null)
		{
			LOGGER.warn("Không tìm thấy OrderDistributor theo code: "+ orderDistributor.getCode());
			return SodResult.<OrderDistributor>builder().isError(true).message("Không tìm thấy OrderDistributor theo code: "+ orderDistributor.getCode()).build();
		}
			
		if(orderDistributor.getStatus().ordinal()< entity.getStatus().ordinal() && !entity.getStatus().equals(OrderDistributorStatus.COMPLETED))
			return SodResult.<OrderDistributor>builder().isError(true).message("Không thể cập nhật ngược trạng thái").build();
		if(!entity.getIsRequestPayment()) { // lúc chưa gửi yêu cầu thanh toán
			entity.setPrice(orderDistributor.getPrice());
			entity.setQuantity(orderDistributor.getQuantity());
			entity.setNote(orderDistributor.getNote());
			if(orderDistributor.getStatus().equals(OrderDistributorStatus.CANCEL))
				entity.setStatus(OrderDistributorStatus.CANCEL);
		} else { // sau khi gửi yêu cầu thanh toán
			if(entity.getTransactionStatus().equals(TransactionStatus.WAITTING) || entity.getTransactionStatus().equals(TransactionStatus.CANCEL)) {
				entity.setNote(orderDistributor.getNote());
				if(orderDistributor.getStatus().equals(OrderDistributorStatus.CANCEL))
					entity.setStatus(OrderDistributorStatus.CANCEL);
			} else {
				entity.setNote(orderDistributor.getNote());
				entity.setStatus(orderDistributor.getStatus());
			}
		}

		OrderDistributor rs = repository.update(entity); 
		return SodResult.<OrderDistributor>builder().result(rs).build();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderDistributorService#filter(com.sodo.xmarketing.dto.OrderDistributorSearch, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<OrderDistributor> filter(OrderDistributorSearch orderDistributorSearch,PageRequest pageable,
			CurrentUser currentUser) {

		return repository.filter(orderDistributorSearch, pageable, currentUser);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderDistributorService#findByCode(java.lang.String)
	 */
	@Override
	public OrderDistributor findByCode(String code) {
		return repository.getByCode(code);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.OrderDistributorService#updateAfterPayment(java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<OrderDistributor> updateTransactionStatus(String code, TransactionStatus status, CurrentUser currentUser) {
		OrderDistributor entity = repository.getByCode(code);
		if(entity == null)
			return SodResult.<OrderDistributor>builder().isError(true).build();
		entity.setTransactionStatus(status);
		entity.setLastModifiedBy(currentUser.getCode());
		entity.setLastModifiedDate(LocalDateTime.now());
		try {
			return SodResult.<OrderDistributor>builder().result(repository.update(entity)).build();
		} catch (Exception e) {
			return SodResult.<OrderDistributor>builder().result(repository.update(entity)).build();
		}
	}
}
