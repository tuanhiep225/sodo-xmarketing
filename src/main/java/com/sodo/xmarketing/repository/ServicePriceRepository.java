/**
 * 
 */
package com.sodo.xmarketing.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.dto.ServicePricingDTO;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.account.CurrentUser;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface ServicePriceRepository extends BaseRepository<ServicePrice, String>,  ServicePriceCustomRepository{

	@Query("{'groupServiceCode': ?0, 'isDelete':false}")
	public Collection<ServicePrice> getByGroupServiceCode(String groupCode);

	@Query("{'code': ?0, 'isDelete':false}")
	public ServicePrice getByCode(String code);


}
