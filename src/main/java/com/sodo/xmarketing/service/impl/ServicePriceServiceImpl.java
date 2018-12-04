/**
 * 
 */
package com.sodo.xmarketing.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.dto.ServicePricingDTO;
import com.sodo.xmarketing.model.Block;
import com.sodo.xmarketing.model.BlockCulture;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.ServicePriceCulture;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.agency.ServicePricingAgency;
import com.sodo.xmarketing.model.agency.UserModel;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.repository.ServicePriceRepository;
import com.sodo.xmarketing.service.CustomerService;
import com.sodo.xmarketing.service.ServicePriceService;
import com.sodo.xmarketing.service.agency.ConvertService;

import lombok.var;

/**
 * @author tuanhiep225
 *
 */
@Service
public class ServicePriceServiceImpl implements ServicePriceService {

	@Autowired
	private ServicePriceRepository servicePriceRepository;
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ConvertService convertService;

	private static final Log LOGGER = LogFactory.getLog(ServicePriceServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.ServicePriceService#create(com.sodo.xmarketing.
	 * model.ServicePrice)
	 */
	@Override
	public ServicePrice create(ServicePrice block) {
		ServicePrice rs = null;
		try {
			rs = servicePriceRepository.add(block);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.ServicePriceService#getAll()
	 */
	@Override
	public Collection<ServicePrice> getAll() {

		Collection<ServicePrice> rs = null;
		try {
			rs = servicePriceRepository.getAll();
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.ServicePriceService#getByCode(java.lang.String)
	 */
	@Override
	public ServicePrice getByCode(String code) {
		ServicePrice rs = null;
		if (null == code || code.isEmpty()) {
			LOGGER.error("Paramater 'code' must not be null or empty !");
			return null;
		}
		try {
			rs = servicePriceRepository.getByCode(code);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.ServicePriceService#getByGroupCode(java.lang.
	 * String)
	 */
	@Override
	public Collection<ServicePrice> getByGroupCode(String groupCode) {

		Collection<ServicePrice> rs = null;
		if (null == groupCode || groupCode.isEmpty()) {
			LOGGER.error("Paramater 'groupCode' must not be null or empty !");
			return null;
		}
		try {
			rs = servicePriceRepository.getByGroupServiceCode(groupCode);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	@Override
	public Page<ServicePrice> get(int page, int pageSize) {
		PageRequest pageable = PageRequest.of(page, pageSize);
		return servicePriceRepository.getAll(pageable);
	}

	@Override
	public ServicePrice getById(String id) {
		ServicePrice rs = null;
		if (null == id || id.isEmpty()) {
			LOGGER.error("Paramater 'id' must not be null or empty !");
			return null;
		}
		try {
			rs = servicePriceRepository.get(id);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.ServicePriceService#create(java.util.Collection)
	 */
	@Override
	public Collection<ServicePrice> create(Collection<ServicePrice> entities) {
		return servicePriceRepository.insert(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.ServicePriceService#updateService(com.sodo.
	 * xmarketing.dto.ServicePricingDTO,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<ServicePrice> updateService(String code, ServicePricingDTO entity, CurrentUser currentUser) {
		if (currentUser == null) {
			return SodResult.<ServicePrice>builder().isError(true).message("Current user invalid !").build();
		}
		ServicePrice rs = null;
		try {
			rs = servicePriceRepository.updateService(code, entity, currentUser);
			return SodResult.<ServicePrice>builder().result(rs).build();
		} catch (Exception e) {
			return SodResult.<ServicePrice>builder().isError(true).message(e.getMessage()).build();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.ServicePriceService#removeServiceByCulture(java.
	 * lang.String, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<ServicePrice> removeServiceByCulture(String code, String culture, CurrentUser currentUser) {
		if (currentUser == null) {
			return SodResult.<ServicePrice>builder().isError(true).message("Current user invalid !").build();
		}
		ServicePrice rs = null;
		try {
			rs = servicePriceRepository.removeServiceByCulture(code, culture, currentUser);
			return SodResult.<ServicePrice>builder().result(rs).build();
		} catch (Exception e) {
			return SodResult.<ServicePrice>builder().isError(true).message(e.getMessage()).build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.ServicePriceService#crate(java.lang.String,
	 * java.lang.String, com.sodo.xmarketing.dto.ServicePricingDTO,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<ServicePrice> crate(String groupCode, String culture, ServicePricingDTO entity,
			CurrentUser currentUser) {
		if(groupCode==null) {
			return SodResult.<ServicePrice>builder().isError(true).message("Paramater groupCode missing !").build();
		}
		if(culture==null) {
			return SodResult.<ServicePrice>builder().isError(true).message("Paramater culture missing !").build();
		}
		if(entity==null) {
			return SodResult.<ServicePrice>builder().isError(true).message("Paramater entity missing !").build();
		}
		if(currentUser == null) {
			return SodResult.<ServicePrice>builder().isError(true).message("Current user invalid !").build();
		}
		
		ServicePrice rs = null;
		Map<String, ServicePriceCulture> servicePriceCulture = new HashMap<>();
		
		servicePriceCulture.put(culture, ServicePriceCulture.builder()
				.name(entity.getName())
				.description(entity.getDescription())
				.miniumOrder(entity.getMiniumOrder())
				.maxOrder(entity.getMaxOrder())
				.build());
		
		
		Map<String,BlockCulture> block = new HashMap<>();
		
		block.put(culture,BlockCulture.builder()
				.price(entity.getPrice())
				.wholesalePrices(entity.getWholesalePrices())
				.priceVip2(entity.getPriceVip2())
				.speed(entity.getSpeed())
				.denominator(entity.getDenominator())
				.unitName(entity.getUnitName())
				.build());
		
		ServicePrice model = ServicePrice.builder()
				.code(entity.getCode())
				.culture(servicePriceCulture)
				.groupServiceCode(groupCode)
				.block(Block.builder().culture(block).build())
				.build();
		try {
			
			rs = servicePriceRepository.add(model);
			return SodResult.<ServicePrice>builder().result(rs).build();
		} catch (Exception e) {
			return SodResult.<ServicePrice>builder().isError(true).message(e.getMessage()).build();
		}

	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.ServicePriceService#checkCode(java.lang.String)
	 */
	@Override
	public SodResult<Boolean> checkCode(String code) {
		if(code==null) {
			return SodResult.<Boolean>builder().isError(true).message("Paramater groupCode missing !").build();
		}
		ServicePrice rs = null;
		try {
			rs = servicePriceRepository.getByCode(code);
			if(rs != null)
				return SodResult.<Boolean>builder().result(true).build();
			return SodResult.<Boolean>builder().result(false).build();
		} catch (Exception e) {
			return SodResult.<Boolean>builder().isError(true).message(e.getMessage()).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.ServicePriceService#getAllForAgency(com.sodo.xmarketing.model.agency.UserModel)
	 */
	@Override
	public SodResult<Collection<ServicePricingAgency>> getAllForAgency(UserModel user) {
		Customer customer = null;
		customer = customerService.findByUsername(user.getUsername());

		if (customer == null) {
			LOGGER.error("ACCOUNT_MISSING, Account missing!");
			return SodResult.<Collection<ServicePricingAgency>>builder().isError(true).message("Account missing !").code("ACCOUNT_MISSING")
					.build();
		}


		if (!passwordEncoder.matches(user.getPassword(), customer.getPassword())) {
			LOGGER.error("PASSWORD_NOT_MATCH, Password not match !");
			return SodResult.<Collection<ServicePricingAgency>>builder().isError(true).message("Password not match !").code("PASSWORD_NOT_MATCH")
					.build();
		}
		var level = customer.getAttribute().toString();
		var culture = customer.getFormat().getLang();
		var rs = servicePriceRepository.getAll().stream().map(x -> convertService.convertServicePricing(x, level, culture)).collect(Collectors.toList());
		return SodResult.<Collection<ServicePricingAgency>>builder().isError(false).result(rs)
				.build();
	}

}
