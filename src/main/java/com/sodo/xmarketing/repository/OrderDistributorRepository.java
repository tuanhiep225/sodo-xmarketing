/**
 * 
 */
package com.sodo.xmarketing.repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.OrderDistributor;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface OrderDistributorRepository extends BaseRepository<OrderDistributor, String>, OrderDistributorCustomRepository{
	
	@Query("{'orderCode':'?0', 'isDelete': false}")
	OrderDistributor getByOrderCode(String orderCode);

	@Query("{'code':'?0', 'isDelete': false}")
	OrderDistributor getByCode(String code);
}
