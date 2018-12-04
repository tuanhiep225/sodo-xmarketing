/**
 * 
 */
package com.sodo.xmarketing.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.dto.ServicePricingDTO;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.ServicePrice;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.agency.ServicePricingAgency;
import com.sodo.xmarketing.model.agency.UserModel;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.service.ServicePriceService;

import io.swagger.annotations.ApiOperation;

/**
 * @author tuanhiep225
 *
 */
@RestController
@RequestMapping("/api/service-price")
public class ServicePriceController {

	@Autowired
	private ServicePriceService servicePrice;
	
	@Autowired
	private CurrentUserService currentUserService;
	
	@PostMapping("")
	public ServicePrice create(@RequestBody ServicePrice entity) {
		return servicePrice.create(entity);
	}
	
	@PostMapping("/collection")
	public Collection<ServicePrice> multiCreate(@RequestBody Collection<ServicePrice> entities) {
		return servicePrice.create(entities);
	}
	
	@GetMapping("/collection")
	public Page<ServicePrice> gets(@RequestParam("page") int page, @RequestParam("page-size") int pageSize) {
		return servicePrice.get(page, pageSize);
	}
	
	@GetMapping("")
	public Collection<ServicePrice> getAll() {
		return servicePrice.getAll();
	}
	
	@GetMapping("/{id}")
	public ServicePrice getById(@PathVariable("id") String id) {
		return servicePrice.getById(id);
	}
	
	@GetMapping("/group-service/{code}")
	public Collection<ServicePrice> getGroupServiceCode(@PathVariable("code") String code) {
		return servicePrice.getByGroupCode(code);
	}
	
	@PutMapping("/{code}")
	public SodResult<ServicePrice> updateService(@RequestBody ServicePricingDTO entity,@PathVariable("code") String code, BindingResult errors){
		if(errors.hasErrors())
			return SodResult.<ServicePrice>builder().isError(true).message(errors.getAllErrors().get(0).getDefaultMessage()).build();
		SodResult<ServicePrice> result = new SodResult<>();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser == null || currentUser.isCustomer()) {
			return SodResult.<ServicePrice>builder().isError(true).message("Current user invalid !").build();
		}
		return servicePrice.updateService(code,entity,currentUser);

	}
	
	@DeleteMapping("/{code}/{culture}")
	public SodResult<ServicePrice> updateService(@PathVariable("code") String code, @PathVariable("culture") String culture){
		SodResult<ServicePrice> result = new SodResult<>();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser == null || currentUser.isCustomer()) {
			return SodResult.<ServicePrice>builder().isError(true).message("Current user invalid !").build();
		}
		return servicePrice.removeServiceByCulture(code,culture,currentUser);

	}
	
	@PostMapping("/{group-code}/{culture}")
	public SodResult<ServicePrice> create(@PathVariable("group-code") String groupCode, @PathVariable("culture") String culture, @RequestBody ServicePricingDTO entity){
		SodResult<ServicePrice> result = new SodResult<>();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser == null || currentUser.isCustomer()) {
			return SodResult.<ServicePrice>builder().isError(true).message("Current user invalid !").build();
		}
		return servicePrice.crate(groupCode, culture, entity,currentUser);

	}
	
	@GetMapping("/{code}/consist")
	public SodResult<Boolean> checkCode(@PathVariable("code") String code){
		SodResult<ServicePrice> result = new SodResult<>();
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser == null || currentUser.isCustomer()) {
			return SodResult.<Boolean>builder().isError(true).message("Current user invalid !").build();
		}
		return servicePrice.checkCode(code);
	}
	
	@ApiOperation(value="Lấy ra tất cả các dịch vụ",notes="Lấy ra tất cả các dịch vụ")
	@PostMapping("/agency")
	public SodResult<Collection<ServicePricingAgency>> getAllForAgency(@RequestBody UserModel user) {
		return servicePrice.getAllForAgency(user);
	}
}
