package com.sodo.xmarketing.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.Deposit;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.service.DepositService;
import com.sodo.xmarketing.utils.ConfigHelper;
import com.sodo.xmarketing.utils.ErrorCode;
import com.sodo.xmarketing.utils.FunctionalUtils;
import com.sodo.xmarketing.utils.Properties;

/*
 * @author killer
 *
 */
@RestController
@RequestMapping("/v1/deposit")
public class DepositController {
	@Autowired
	private DepositService depositService;
	@Autowired
	private Properties properties;
	@Autowired
	private CurrentUserService currentUserService;
	@Autowired
	private ConfigHelper configHelper;

	private Sort sort = new Sort(Sort.Direction.DESC, "createdDate");

	/**
	 * create Deposit
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Deposit> create(@Valid @RequestBody Deposit deposit, BindingResult errors)
			throws SodException {
		if (errors.hasErrors()) {
			throw new SodException(errors.getAllErrors().get(0).getDefaultMessage(),
					errors.getAllErrors().get(0).getCode());
		}
		Deposit result = null;
		result = depositService.createDeposit(deposit, currentUserService.getCurrentUser());
		if (result == null) {
			throw new SodException("Created deposit failed", errors.getAllErrors().get(0).getCode());
		}
		return new ResponseEntity<>(result, HttpStatus.CREATED);

	}

	/**
	 * update deposit
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Deposit> update(@Valid @RequestBody Deposit deposit, BindingResult errors)
			throws SodException {

		Deposit depositById = depositService.findOne(deposit.getId());
		if (depositById == null) {
			throw new SodException("NOT FOUND", "deposit not found");
		}
		if (!FunctionalUtils.isExpried(depositById.getLastModifiedDate(), deposit.getLastModifiedDate())) {
			throw new SodException(ErrorCode.EXPIRED_DATE_UPDATE.getReasonPhrase(),
					ErrorCode.EXPIRED_DATE_UPDATE.name());
		}

		if (!depositById.getStatus().equals(TransactionStatus.WAITTING.name())) {
			throw new SodException(ErrorCode.RESOLVED.getReasonPhrase(), ErrorCode.RESOLVED.name());
		}

		Deposit updatedDeposit = depositService.updateDeposit(deposit, currentUserService.getCurrentUser());
		return new ResponseEntity<>(updatedDeposit, HttpStatus.OK);
	}

	/**
	 * get list deposit paging
	 *
	 * @return list deposit
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getall")
	public ResponseEntity<List<Deposit>> getAllDeposit(@RequestParam("page") int page, @RequestParam("size") int size) {
		// Pageable and Sort
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		List<Deposit> deposits = depositService.getAllDeposit(pageRequest);
		return new ResponseEntity<>(deposits, HttpStatus.OK);
	}

	/**
	 * get Deposit by id
	 *
	 * @return Deposit
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getById")
	public ResponseEntity<Deposit> getById(@RequestParam("id") String id) throws SodException {
		Deposit deposit = depositService.findOne(id);
		if (deposit == null) {
			throw new SodException("NOT FOUND", "Deposit not found");
		}
		return new ResponseEntity<>(deposit, HttpStatus.OK);
	}

	@GetMapping("/{code}")
	public ResponseEntity<Deposit> getByCode(@PathVariable("code") String code) throws SodException {
		Deposit deposit = depositService.getByCode(code);
		if (deposit == null) {
			throw new SodException("NOT FOUND", "Deposit not found");
		}
		return new ResponseEntity<>(deposit, HttpStatus.OK);
	}

	@RequestMapping(value = "/accept-payment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Deposit> acceptPayment(@RequestBody Deposit deposit) throws SodException {
		Deposit tobeDeposit = depositService.findOne(deposit.getId());

		Map<String, Object> value = new HashMap();
		value.put("lastModifiedDate", LocalDateTime.now());
		depositService.updateField(deposit.getCode(), currentUserService.getCurrentUser(), "", "lastModifiedDate",
				value);

		if (tobeDeposit == null) {
			throw new SodException("Not found Deposit", "search");
		}

		Deposit finaDeposit = new Deposit();
		return new ResponseEntity<>(finaDeposit, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> getDepositByFilter(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "query", required = false) String querry,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "direction", required = false) String direction) throws SodException {
		// Pageable and Sort
		PageRequest request = null;
		if (sort != null) {
			if (direction.equals("asc")) {
				request = PageRequest.of(page, size, new Sort(Sort.Direction.ASC, sort));
			} else if (direction.equals("desc")) {
				request = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, sort));
			}

		} else {

			request = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "lastModifiedDate"));

		}

		Gson gson = new Gson();
		JsonObject queryParam = gson.fromJson(querry, JsonObject.class);
		String startDate = queryParam.get("startDate").getAsString();
		String endDate = queryParam.get("endDate").getAsString();
		String customerAccount = queryParam.get("userName").getAsString();
		String staffCode = queryParam.get("staffCode").getAsString();
		String status = queryParam.get("transactionStatus").getAsString();
		String senderName = queryParam.get("senderName").getAsString();
		String cardNumber = queryParam.get("cardNumber").getAsString();

		Map<String, Object> deposits = depositService.geDepositByFilter(customerAccount, staffCode, senderName,
				cardNumber, startDate, endDate, status, request);
		return new ResponseEntity<>(deposits, HttpStatus.OK);
	}
	
	
	@PutMapping("/cancel")
	public Deposit cancel(@RequestBody Deposit deposit) {
		return depositService.acceptOrRefuse(deposit.getCode(), TransactionStatus.CANCEL.name(), currentUserService.getCurrentUser());
	}

}
