/**
 * 
 */
package com.sodo.xmarketing.repository.impl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.sodo.xmarketing.dto.ServicePricingDTO;
import com.sodo.xmarketing.model.BlockCulture;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.ServicePriceCulture;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.repository.ServicePriceCustomRepository;

/**
 * @author tuanhiep225
 *
 */
public class ServicePriceRepositoryImpl implements ServicePriceCustomRepository{

//	private static final Log LOGGER = LogFactory.getLog(ServicePriceRepositoryImpl.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.ServicePriceCustomRepository#update(java.lang.String, com.sodo.xmarketing.dto.ServicePricingDTO, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public ServicePrice updateService(String code, ServicePricingDTO entity, CurrentUser currentUser) {
		Query query = new Query();
		Update update = new Update();
		ServicePriceCulture servicePriceCulture = ServicePriceCulture.builder()
				.description(entity.getDescription())
				.name(entity.getName())
				.miniumOrder(entity.getMiniumOrder())
				.maxOrder(entity.getMaxOrder())
				.minMinute(entity.getMinMinute())
				.allowTrial(entity.getAllowTrial())
				.build();
		BlockCulture block = BlockCulture.builder()
				.price(entity.getPrice())
				.wholesalePrices(entity.getWholesalePrices())
				.priceVip2(entity.getPriceVip2())
				.unitName(entity.getUnitName())
				.speed(entity.getSpeed())
				.denominator(entity.getDenominator())
				.build();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code").is(code));
		update.set("culture."+entity.getCulture(), servicePriceCulture);
		update.set("block.culture."+entity.getCulture(), block);
		
		return mongoTemplate.findAndModify(query, update, ServicePrice.class);
		
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.ServicePriceCustomRepository#removeServiceByCulture(java.lang.String, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public ServicePrice removeServiceByCulture(String code, String culture, CurrentUser currentUser) {
		Query query = new Query();
		Update update = new Update();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code").is(code));
		update.unset("culture."+culture);
		update.unset("block.culture."+culture);
		return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), ServicePrice.class);
	}

}
