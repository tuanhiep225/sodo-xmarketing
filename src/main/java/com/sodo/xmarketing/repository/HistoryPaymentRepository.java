/**
 * 
 */
package com.sodo.xmarketing.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.HistoryPayment;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface HistoryPaymentRepository extends BaseRepository<HistoryPayment, String> {

	@Query("{'username':?0, 'isDelete':false}")
	Collection<HistoryPayment> getByUsername(String username);

	@Query("{'username':?0, 'isDelete':false}")
	Page<HistoryPayment> getByUsername(String username, Pageable page);

	
	@Query("{'order.id':?0, 'isDelete':false}")
	HistoryPayment getByOrderId(String orderId);

}
