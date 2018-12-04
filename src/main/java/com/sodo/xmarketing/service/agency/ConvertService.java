/**
 * 
 */
package com.sodo.xmarketing.service.agency;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.agency.OrderAgencyData;
import com.sodo.xmarketing.model.agency.ServicePricingAgency;
import com.sodo.xmarketing.status.CustomerStatus;

/**
 * @author tuanhiep225
 *
 */
@Service
public class ConvertService {
	public ServicePricingAgency convertServicePricing (ServicePrice model, String level, String culture) {
		ServicePricingAgency result = ServicePricingAgency.builder()
				.code(model.getCode())
				.denominator(model.getBlock().getCulture().get(culture).getDenominator())
				.unitName(model.getBlock().getCulture().get(culture).getUnitName())
				.speed(model.getBlock().getCulture().get(culture).getSpeed())
				.maxOrder(model.getCulture().get(culture).getMaxOrder())
				.miniumOrder(model.getCulture().get(culture).getMiniumOrder())
				.name(model.getCulture().get(culture).getName())
				.description(model.getCulture().get(culture).getDescription())
				.build();
		if(CustomerStatus.NORMAL.toString().equals(level)) {
			result.setPrice(model.getBlock().getCulture().get(culture).getPrice());
		} else if(CustomerStatus.VIP1.toString().equals(level)) {
			result.setPrice(model.getBlock().getCulture().get(culture).getWholesalePrices());
		}  else if(CustomerStatus.VIP2.toString().equals(level)) {
			result.setPrice(model.getBlock().getCulture().get(culture).getPriceVip2());
		}
		
		return result;
	}
	
	public OrderAgencyData convertOrder(Order model, String level, String culture) {
		return OrderAgencyData.builder()
								.code(model.getCode())
								.price(model.getPrice())
								.quantity(model.getQuantity())
								.url(model.getUrl())
								.createdDate(model.getCreatedDate())
								.status(model.getStatus())
								.service(convertServicePricing(model.getService(), level, culture))
								.start(model.getStart())
								.current(model.getCurrent())
								.build();
	}
}
