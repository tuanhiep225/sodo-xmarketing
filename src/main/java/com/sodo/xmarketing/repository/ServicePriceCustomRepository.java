/**
 * 
 */
package com.sodo.xmarketing.repository;
import com.sodo.xmarketing.dto.ServicePricingDTO;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.account.CurrentUser;

/**
 * @author tuanhiep225
 *
 */
public interface ServicePriceCustomRepository{
	public ServicePrice updateService(String code, ServicePricingDTO entity, CurrentUser currentUser);
	/**
	 * @param code
	 * @param culture
	 * @param currentUser
	 * @return
	 */
	public ServicePrice removeServiceByCulture(String code, String culture, CurrentUser currentUser);

}
