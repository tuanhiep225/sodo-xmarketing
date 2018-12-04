package com.sodo.xmarketing.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.dto.FundSearchDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.common.SearchResultMap;
import com.sodo.xmarketing.model.fund.Fund;
import com.sodo.xmarketing.service.FundService;
import com.sodo.xmarketing.utils.FunType;
import com.sodo.xmarketing.utils.Properties;
import com.sodo.xmarketing.utils.StringUtils;

import io.swagger.annotations.ApiOperation;

/*
 * author killer
 */
@RestController
@RequestMapping(value = "v1/fund")
public class FundController {

	@Autowired
	private FundService fundService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private Properties properties;

	@RequestMapping(method = RequestMethod.POST, value = "/create",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Fund> create(@Valid @RequestBody Fund fund, BindingResult errors)
			throws SodException {
		// nothing
		if (errors.hasErrors()) {
			throw new SodException(errors.getAllErrors().get(0).getDefaultMessage(),
					errors.getAllErrors().get(0).getCode());
		}

		fund.setTextSearch(StringUtils.unAccent(fund.getName()).toLowerCase());

		Fund result;

		result = fundService.create(fund);

		if (result == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@GetMapping(value = "fund-filter")
	public Map<String, Object> fundFilter(@RequestParam int page, @RequestParam int size,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "direction", required = false) String direction)
			throws SodException {

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
		request = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "lastModifiedDate"));
		Gson gson = new Gson();
		JsonObject queryParam = gson.fromJson(query, JsonObject.class);
		String code= queryParam.get("code").getAsString();
		String type = queryParam.get("fund_type").getAsString();
		String currency = queryParam.get("currency").getAsString();
		String name = queryParam.get("name").getAsString();
		String managerCode = queryParam.get("manager").getAsString();
		String country = queryParam.get("country").getAsString();
		String enabled = queryParam.get("status").getAsString();
		String currentUserCode = queryParam.get("currentUserCode").getAsString();

		return fundService.filterFund(code, type, currency, name, managerCode,
				country, enabled, request, currentUserCode);
	}

	@RequestMapping(value = "/check-duplicate", method = RequestMethod.GET)
	public ResponseEntity<Fund> checkDuplicate(
			@RequestParam(value = "fieldName", required = false) String fieldName,
			@RequestParam(value = "value", required = false) String value) throws SodException {
		Fund fund = fundService.checkDuplicate(fieldName, value);

		return new ResponseEntity<>(fund, HttpStatus.OK);
	}


	@RequestMapping(value = "/getMapByFundGroupCode", method = RequestMethod.GET)
	@ApiOperation(value = "Lấy danh sách quỹ nhóm theo nhóm quỹ")
	public ResponseEntity<SearchResultMap<Fund>> getMapByFundGroupCode(
			@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam(value = "fundType", required = false) String fundType,
			@RequestParam(value = "currency", required = false) String currency,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "manager", required = false) String manager,
			@RequestParam(value = "status", required = false) Boolean status,
			@RequestParam(value = "fundGroupCode", required = false) String fundGroupCode) {

		FundSearchDTO fundSearch = new FundSearchDTO();
		fundSearch.setFundType(fundType);
		fundSearch.setCurrency(currency);
		fundSearch.setName(name);
		fundSearch.setManager(manager);
		fundSearch.setStatus(status);
		fundSearch.setFundGroupCode(fundGroupCode);

		return new ResponseEntity<>(fundService.getMapByFundGroupCode(fundSearch, page, size),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/suggest-fund", method = RequestMethod.GET)
	public ResponseEntity<List<Fund>> suggestFund(
			@RequestParam(value = "numberRecord", required = false) int numberRecord,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "employeeCode", required = false) String employeeCode)
			throws SodException {
		List<Fund> funds = fundService.suggestFund(query, numberRecord, employeeCode);

		return new ResponseEntity<>(funds, HttpStatus.OK);
	}
	
	  @GetMapping(value = "/get-fund-for-payment-request")
	  public List<Fund> getByGroupCodeAndEmployeeCode(@RequestParam("groupCode") String groupCode) {
	    String employeeCode = currentUserService.getCurrentUser().getCode();
	   List<Fund> funds = fundService.getByGroupCodeAndEmployeeCode(groupCode, employeeCode);
	    return funds;
	  }

}
