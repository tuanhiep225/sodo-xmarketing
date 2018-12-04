/**
 * 
 */
package com.sodo.xmarketing.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.dto.OrderDTO;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.OrderExcel;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.agency.OrderAgencyData;
import com.sodo.xmarketing.model.agency.OrderLiveStreamModel;
import com.sodo.xmarketing.model.agency.OrderModel;
import com.sodo.xmarketing.model.agency.UserModel;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.service.OrderService;
import com.sodo.xmarketing.utils.FunctionalUtils;
import com.sodo.xmarketing.utils.Properties;

import io.swagger.annotations.ApiOperation;

/**
 * @author tuanhiep225
 *
 */

@RestController
@RequestMapping("/api/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private CurrentUserService currentUserService;
	
	@Autowired
	Properties properties;

	@PostMapping("")
	public SodResult<Order> create(@RequestBody Order entity) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return orderService.create(entity, currentUser);
	}

	
	@GetMapping("/{code}")
	public SodResult<Order> getByCode(@PathVariable("code") String code) {
		return orderService.getByCode(code);
	}

	@PutMapping("/{code}")
	public SodResult<Order> update(@RequestBody OrderDTO order, @PathVariable("code") String code) {
		CurrentUser  currentUser = currentUserService.getCurrentUser();
		return orderService.update(order, code, currentUser);
	}

	@GetMapping("/collection")
	public SodResult<Page<Order>> gets(@RequestParam("page") int page, @RequestParam("page-size") int pageSize) {
		return orderService.get(page, pageSize);
	}

	@GetMapping("")
	public SodResult<Collection<Order>> getAll() {
		return orderService.getAll();
	}

	@PostMapping("/collection")
	public SodResult<Collection<Order>> multiCreate(@RequestBody Collection<Order> entities) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return orderService.creates(entities, currentUser);
	}

	@GetMapping("/status")
	public Map<String, Long> countByStatus() {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return orderService.groupByStatusAndCount(currentUser.getUserName());
	}

	@GetMapping("/status/{username}")
	public Map<String, Long> countByStatus(@PathVariable("username") String username) {
		return orderService.groupByStatusAndCount(username);
	}
	
	@GetMapping("/filter")
	public SodSearchResult<Order> filter(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestParam(value = "param", required = false) String param,
			@RequestParam(value = "keyword", required = false) String keyword) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		return orderService.filter(param, keyword, pageable, currentUser);
	}
	
	@GetMapping("/cms/filter-revice")
	@Secured("RECEIVE_ORDER")
	@Deprecated
	public SodSearchResult<Order> filterReceive(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestParam(value = "param", required = false) String param,
			@RequestParam(value = "keyword", required = false) String keyword) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
			if(currentUser.isCustomer()) {
				return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
			}
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		return orderService.filterForCMS(param, keyword, pageable, currentUser);
	}
	
	@GetMapping("/cms/filter/my-order")
	@Secured("MY_ORDER")
	@Deprecated
	public SodSearchResult<Order> filterMyOrder(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestParam(value = "param", required = false) String param,
			@RequestParam(value = "keyword", required = false) String keyword) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
			if(currentUser.isCustomer()) {
				return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
			}
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "timeReceive"));
		return orderService.filterForMyOrder(param, keyword, pageable, currentUser);
	}
	
	@PostMapping("/cms/receive")
	public SodResult<Boolean> receive(@RequestBody Collection<Order> entities) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser.isCustomer()) {
			return SodResult.<Boolean>builder().isError(true).message("Current User not found !").build();
		}
		return orderService.receive(entities, currentUser);
	}
	
	@PostMapping("/cms/refund/{code}")
	public SodResult<Boolean> refund(@RequestBody OrderDTO entity,@PathVariable("code") String code) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		if(currentUser.isCustomer()) {
			return SodResult.<Boolean>builder().isError(true).message("Current User not found !").build();
		}
		return orderService.refund(code,entity, currentUser);
	}
	
	@GetMapping("/exists")
	public Boolean checkOrderFree() {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return orderService.checkOrderFree(currentUser);
	}
	
	@PostMapping("/trials")
	public SodResult<Order> createOrderTrial(@RequestBody Order entity) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return orderService.createOrderTrial(entity, currentUser);
	}

	@GetMapping("/count/status")
	public Map<String, Long> cmsCountByStatus(@RequestParam(value="isToday", required = false) Boolean isToday, @RequestParam("role") String role){
		CurrentUser currentUser = currentUserService.getCurrentUser();
		return orderService.cmsCountByStatus(isToday, role, currentUser);
	}
	
	@GetMapping("/turnover")
	public Map<String, BigDecimal> cmsTurnover(@RequestParam(value="date", required = false) String date, @RequestParam("role") String role) throws SodException{
		CurrentUser currentUser = currentUserService.getCurrentUser();
		LocalDate dateTime = null;
		if(date != null) {
			try {
				dateTime = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} catch (Exception e) {
				throw new SodException("Can't convert date","CONVERT_DATE");
			}
			
		}
		return orderService.cmsTurnover(dateTime, role, currentUser);
	}
	
	@GetMapping("/filter-v2")
	public SodSearchResult<Order> filterV2(@RequestParam("page") int page, @RequestParam("page-size") int pageSize,
			@RequestParam(value = "param", required = false) String param,
			@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value="role", required= false) String role) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		return orderService.filterV2(param, keyword, pageable, currentUser, role);
	}
	
	@ApiOperation(value="Tạo đơn hàng")
	@PostMapping("/agency")
	public SodResult<String> createForAgency(@RequestBody OrderModel entity) throws SodException {
		return orderService.createForAgency(entity);
	}
	
	@ApiOperation(value="Tạo đơn hàng")
	@PostMapping("/agency/live-stream")
	public SodResult<String> createOrderLiveStreamForAgency(@RequestBody OrderLiveStreamModel entity) throws SodException {
		return orderService.createOrderLiveStreamForAgency(entity);
	}
	
	@ApiOperation(value="Lấy đơn hàng theo mã đơn")
	@PostMapping("/agency/{order-code}")
	public SodResult<OrderAgencyData> getOneForAgency(@RequestBody UserModel entity, @PathVariable("order-code") String orderCode) throws SodException {
		return orderService.getOneForAgency(entity, orderCode);
	}
	
	@ApiOperation(value="Lấy ra tất cả các đơn hàng")
	@PostMapping("/agency/getAll")
	public SodResult<Collection<OrderAgencyData>> getAllForAgency(@RequestBody UserModel entity) throws SodException {
		return orderService.getAllForAgency(entity);
	}
	
	@PostMapping("/filter-v3")
	public SodSearchResult<Order> filterV3(@RequestParam("page") int page, @RequestParam("page-size") int pageSize, @RequestBody OrderSearch orderSearch) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		return orderService.filterV3(orderSearch, pageable, currentUser);
	}
	
	@PostMapping("/cms/filter-revice")
	public SodSearchResult<Order> filterReceiveV3(@RequestParam("page") int page, @RequestParam("page-size") int pageSize, @RequestBody OrderSearch orderSearch) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
			if(currentUser.isCustomer()) {
				return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
			}
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		return orderService.filterReceiveV3(orderSearch, pageable, currentUser);
	}
	
	@PostMapping("/cms/filter/my-order")
	@Secured("MY_ORDER")
	public SodSearchResult<Order> filterMyOrderV3(@RequestParam("page") int page, @RequestParam("page-size") int pageSize, @RequestBody OrderSearch orderSearch) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
			if(currentUser.isCustomer()) {
				return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
			}
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "timeReceive"));
		return orderService.filterForMyOrderV3(orderSearch, pageable, currentUser);
	}
	
	@PostMapping("/cms/filter/order-for-sales")
	@Secured("ORDER_FOR_SALE")
	public SodSearchResult<Order> filterOrderForSales(@RequestParam("page") int page, @RequestParam("page-size") int pageSize, @RequestBody OrderSearch orderSearch) {
		CurrentUser currentUser = currentUserService.getCurrentUser();
			if(currentUser.isCustomer()) {
				return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
			}
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		return orderService.filterOrderForSales(orderSearch, pageable, currentUser);
	}
	
	
	@PostMapping("/export-excel-filter-v3")
	@Produces(MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public  ResponseEntity<byte[]> exportExcelForV3(@RequestParam("page") int page, @RequestParam("page-size") int pageSize, @RequestBody OrderSearch orderSearch) throws SodException, IOException {
		CurrentUser currentUser = currentUserService.getCurrentUser();
		PageRequest pageable = PageRequest.of(page, pageSize,new Sort(Sort.Direction.DESC, "createdDate"));
		SodSearchResult<OrderExcel> rs = orderService.filterV3ForExcel(orderSearch, pageable, currentUser);
		
		Map<String, String> mapFieldNames = new LinkedHashMap<>();
	    mapFieldNames.put("code", "Mã");
	    mapFieldNames.put("url", "URL");
	    mapFieldNames.put("service", "Mã dịch vụ");
	    mapFieldNames.put("quantity", "Số lượng");
	    mapFieldNames.put("price", "Giá");
	    mapFieldNames.put("format", "Tiền tệ");
	    mapFieldNames.put("username", "Tài khoản khách hàng");
	    mapFieldNames.put("staff", "Nhân viên xử lý");
	    mapFieldNames.put("sale", "Nhân viên sale");
	    mapFieldNames.put("start", "Số lượng bắt đầu");
	    mapFieldNames.put("current", "Số lượng hiện tại");
	    mapFieldNames.put("status", "Trạng thái");
	    mapFieldNames.put("createdDate", "Thời gian tạo");
	    mapFieldNames.put("distributorCode", "Mã đơn nhà cung cấp");
	    mapFieldNames.put("fundCode", "Mã quỹ thực hiện");
	    mapFieldNames.put("fundName", "Tên quỹ");
	    mapFieldNames.put("priceNCC", "Giá trị thanh toán");
	    mapFieldNames.put("quantityNCC", "Số lượng");


	    String fileName = "FundTransaction_" + LocalDate.now().toString() + ".xlsx";
	    String filePath = properties.getPathToSave() + fileName;
	   // String filePath = "D:\\" + fileName;

	    try {
	      FunctionalUtils.exportExcel(rs.getItems(), mapFieldNames, filePath,
	          currentUserService.getTimeZone());
	    } catch (Exception e) {
	      e.printStackTrace();
	      throw new SodException("Can not export excel file for Fund Transaction Excel", "EXPORT");
	    }

	    File file = new File(filePath);
	    byte[] document = FileCopyUtils.copyToByteArray(file);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(new MediaType("application", "vnd.ms-excel"));
	    headers.set("Content-Disposition", "attachment; filename= " + fileName);
	    headers.setContentLength(document.length);
	    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
	    headers.add("Pragma", "no-cache");
	    headers.add("Expires", "0");

	    return new ResponseEntity<>(document, HttpStatus.OK);
	}
}
