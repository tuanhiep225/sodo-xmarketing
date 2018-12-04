/**
 * 
 */
package com.sodo.xmarketing.service;

import java.util.Collection;

import org.springframework.data.domain.Page;

import com.sodo.xmarketing.dto.ServicePricingDTO;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.agency.ServicePricingAgency;
import com.sodo.xmarketing.model.agency.UserModel;
import com.sodo.xmarketing.model.common.SodResult;

/**
 * @author tuanhiep225
 *
 */
public interface ServicePriceService {
	public ServicePrice create(ServicePrice block);
	public Collection<ServicePrice> getAll();
	public ServicePrice getByCode(String code);
	public Collection<ServicePrice> getByGroupCode(String groupCode);
	public Page<ServicePrice> get(int page, int pageSize);
	public ServicePrice getById(String id);
	/**
	 * @param entities
	 * @return
	 */
	public Collection<ServicePrice> create(Collection<ServicePrice> entities);
	/**
	 * @param code 
	 * @param entity
	 * @param currentUser
	 * @return
	 */
	public SodResult<ServicePrice> updateService(String code, ServicePricingDTO entity, CurrentUser currentUser);
	/**
	 * @param code
	 * @param culture
	 * @param currentUser
	 * @return
	 */
	public SodResult<ServicePrice> removeServiceByCulture(String code, String culture, CurrentUser currentUser);
	/**
	 * @param groupCode
	 * @param culture
	 * @param entity
	 * @param currentUser
	 * @return
	 */
	public SodResult<ServicePrice> crate(String groupCode, String culture, ServicePricingDTO entity,
			CurrentUser currentUser);
	/**
	 * @param code
	 * @return
	 */
	public SodResult<Boolean> checkCode(String code);
	/**
	 * @param user
	 * @return
	 */
	public SodResult<Collection<ServicePricingAgency>> getAllForAgency(UserModel user);
}
