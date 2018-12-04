/**
 * 
 */
package com.sodo.xmarketing.controller;

import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.dto.OrderDistributorSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.OrderDistributor;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TargetObject;
import com.sodo.xmarketing.model.wallet.TargetObjectType;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.service.FundDeterminantService;
import com.sodo.xmarketing.service.FundTransactService;
import com.sodo.xmarketing.service.OrderDistributorService;

/**
 * @author tuanhiep225
 *
 */
@RestController
@RequestMapping("/api/v1/order-distributor")
public class OrderDistributorController {

	@Autowired
	private OrderDistributorService service;
	
	@Autowired
	private CurrentUserService currentUserService;
	
	  @Autowired
	  FundDeterminantService fundDeterminantService;
	  
		@Autowired
		FundTransactService fundTransactService;
	
	private static final Log LOGGER = LogFactory.getLog(OrderDistributorController.class);

	@PostMapping("/create")
	public OrderDistributor create(@RequestBody OrderDistributor orderDistributor) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return  service.create(orderDistributor, currentUser);
	}
	
	@PutMapping("/update")
	public SodResult<OrderDistributor> update(@RequestBody OrderDistributor orderDistributor) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return  service.update(orderDistributor, currentUser);
	}

	@PostMapping("/cms/filter")
	public SodSearchResult<OrderDistributor> filter(@RequestParam("page") int page,
			@RequestParam("page-size") int pageSize, @RequestBody OrderDistributorSearch orderDistributorSearch) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		return  service.filter(orderDistributorSearch,pageable, currentUser);
	}
	
	@GetMapping("/{orderCode}")
	public OrderDistributor getByOrderCode(@PathVariable("orderCode") String orderCode) {
		return service.getByOrderCode(orderCode);
	}
	
	@GetMapping("/{code}/request-payment")
	public OrderDistributor requestPayment(@PathVariable("code") String code) throws SodException {
		OrderDistributor orderDistributor = service.requestPayment(code);
		if(orderDistributor.getFundInfo() == null)
			return orderDistributor;
		CurrentUser currentUser = currentUserService.getCurrentUser();
		 FundDeterminant fundDeterminant = fundDeterminantService.findByCode("ALIPAY");
		 Determinant determinant = Determinant.builder().code(fundDeterminant.getCode()).name(fundDeterminant.getName()).treeCode(fundDeterminant.getTreeCode()).build();
		FundTransaction fundTransaction = new FundTransaction();
		fundTransaction.setAmount(orderDistributor.getPrice());
		fundTransaction.setFund(orderDistributor.getFundInfo());
		fundTransaction.setFormat(orderDistributor.getFormat());
		fundTransaction.setTransactionType(TransactionType.CREDIT.name());
		fundTransaction.setCreatedBy(currentUser.getUserName());
		fundTransaction.setContent("Thanh toán đơn nhà cung cấp của đơn hàng " + orderDistributor.getOrderCode());
		fundTransaction.setDeterminant(determinant);
		fundTransaction.setCreatedDate(LocalDateTime.now());
		fundTransaction.setLastModifiedDate(LocalDateTime.now());
		fundTransaction.setTarget(TargetObject.builder().code(orderDistributor.getCode()).type(TargetObjectType.ORDER_DISTRIBUTOR).build());
		fundTransaction.setStatus(TransactionStatus.WAITTING.name());
		
		FundTransaction result = null;
		result = fundTransactService.create(fundTransaction, currentUserService.getCurrentUser());
		if (result == null) {
			throw new SodException("Xảy ra lỗi khi tạo phiếu giao dịch quỹ",
					HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		SodResult<FundTransaction> rs = fundTransactService.acceptPayment(result, currentUserService.getCurrentUser());
		service.updateTransactionStatus(code, TransactionStatus.COMPLETED, currentUser);
		return orderDistributor;
	}
	
	@GetMapping("/find-by-code")
	public OrderDistributor findByCode(@RequestParam("code") String code) {
		return service.findByCode(code);
	}
	
	@PutMapping("/update-status")
	public SodResult<OrderDistributor> updateStatus(@RequestParam("code") String code, @RequestParam("status") TransactionStatus status) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return  service.updateTransactionStatus(code,status, currentUser);
	}
}
