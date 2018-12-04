package com.sodo.xmarketing.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.constants.Constants.FundCategories;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.service.CategoriesService;
import com.sodo.xmarketing.service.FundDeterminantService;
import com.sodo.xmarketing.service.FundService;
import com.sodo.xmarketing.service.FundTransactService;
import com.sodo.xmarketing.service.OrderDistributorService;
import com.sodo.xmarketing.utils.ErrorCode;
import com.sodo.xmarketing.utils.FunctionalUtils;
import com.sodo.xmarketing.utils.Properties;

@RestController
@RequestMapping(value = "/api/v1/fund-transact")
public class FundTransactController {

	@Autowired
	FundTransactService fundTransactService;

	@Autowired
	CurrentUserService currentUserService;

	@Autowired
	Properties properties;
	@Autowired
	FundDeterminantService fundDeterminantService;
	@Autowired
	private FundService fundService;

	@Autowired
	private CategoriesService categoriesService;

	@Autowired
	private OrderDistributorService orderDistributorService;
	

	/**
	 *
	 * @param fund
	 * @return
	 * @throws SodException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FundTransaction> create(@Valid @RequestBody FundTransaction fundTransact,
			BindingResult errors) throws SodException {

		if (errors.hasErrors()) {
			throw new SodException(errors.getAllErrors().get(0).getDefaultMessage(),
					errors.getAllErrors().get(0).getCode());
		}
		FundTransaction result = null;
		result = fundTransactService.create(fundTransact, currentUserService.getCurrentUser());
		if (result == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(result, HttpStatus.CREATED);

	}

	/**
	 *
	 * @param fund
	 * @return
	 * @throws SodException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/create-and-accept", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SodResult<FundTransaction>> createAndAccept(@Valid @RequestBody FundTransaction fundTransact,
			BindingResult errors) throws SodException {

		if (errors.hasErrors()) {
			throw new SodException(errors.getAllErrors().get(0).getDefaultMessage(),
					errors.getAllErrors().get(0).getCode());
		}

		FundTransaction result = null;
		result = fundTransactService.create(fundTransact, currentUserService.getCurrentUser());
		if (result == null) {
			throw new SodException("Xảy ra lỗi khi tạo phiếu giao dịch quỹ",
					HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		SodResult<FundTransaction> rs = fundTransactService.acceptPayment(result, currentUserService.getCurrentUser());
		return new ResponseEntity<>(rs, HttpStatus.OK);
	}

	@RequestMapping(value = "/get-by-code", method = RequestMethod.GET)
	public ResponseEntity<FundTransaction> getFundByCode(@RequestParam(value = "code", required = false) String code)
			throws SodException {
		FundTransaction fundTransact = fundTransactService.getByCode(code.toUpperCase());
		if (fundTransact == null) {
			throw new SodException("Not Found Fund", "fund");
		}
		return new ResponseEntity<>(fundTransact, HttpStatus.OK);
	}

	@RequestMapping(value = "/suggest-fund", method = RequestMethod.GET)
	public ResponseEntity<List<FundTransaction>> suggestFundTransact(
			@RequestParam(value = "numberRecord", required = false) int numberRecord,
			@RequestParam(value = "query", required = false) String query) throws SodException {
		List<FundTransaction> funds = fundTransactService.suggestFundTransact(query.toLowerCase(), numberRecord);

		return new ResponseEntity<>(funds, HttpStatus.OK);
	}

	/**
	 * get fundTransacts by paging + sort value+direction + querry by
	 */
	@RequestMapping(value = "/fund-transact-filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> getFundTransactFilter(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "direction", required = false) String direction) throws SodException {
		// Pageable and Sort
		PageRequest request = null;

		request = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "lastModifiedDate"));
		Gson gson = new Gson();
		JsonObject queryParam = gson.fromJson(query, JsonObject.class);
		String code = queryParam.get("code").getAsString();
		String objectType = queryParam.get("objectType").getAsString();
		String objectCode = queryParam.get("objectCode").getAsString();
		String fundCode = queryParam.get("fundCode").getAsString();
		String transactionType = queryParam.get("transactionType").getAsString();
		String determinantEntryCode = queryParam.get("determinantEntryCode").getAsString();
		String currency = queryParam.get("currency").getAsString();
		String status = queryParam.get("status").getAsString();
		String amountFrom = queryParam.get("amountFrom").getAsString();
		String amountTo = queryParam.get("amountTo").getAsString();
		String createdDateFrom = queryParam.get("createdDateFrom").getAsString();
		String createdDateTo = queryParam.get("createdDateTo").getAsString();
		String timeReportFrom = queryParam.get("timeReportFrom").getAsString();
		String timeReportTo = queryParam.get("timeReportTo").getAsString();
		Boolean absoluteDeter = queryParam.get("absoluteDeter").getAsBoolean();

		Map<String, Object> fundTransacts = fundTransactService.filterFundTransact(timeReportFrom, timeReportTo,
				absoluteDeter, code, objectType, objectCode, fundCode, transactionType, determinantEntryCode, currency,
				status, amountFrom, amountTo, createdDateFrom, createdDateTo, request);
		return new ResponseEntity<>(fundTransacts, HttpStatus.OK);
	}

	@RequestMapping(value = "/accept-payment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SodResult<FundTransaction>> acceptPayment(@RequestBody FundTransaction fundTransact)
			throws SodException {

		return new ResponseEntity<>(
				fundTransactService.acceptPayment(fundTransact, currentUserService.getCurrentUser()),
				HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FundTransaction> update(@RequestBody FundTransaction fundTransact, BindingResult errors)
			throws SodException {
		// nothing
		if (errors.hasErrors()) {
			throw new SodException(errors.getAllErrors().get(0).getDefaultMessage(),
					errors.getAllErrors().get(0).getCode());
		}
		FundTransaction findTransact = fundTransactService.getByCode(fundTransact.getCode());
		// if (!FunctionalUtils.isExpried(findTransact.getLastModifiedDate(),
		// fundTransact.getLastModifiedDate())) {
		// throw new SodException(ErrorCode.EXPIRED_DATE_UPDATE.getReasonPhrase(),
		// ErrorCode.EXPIRED_DATE_UPDATE.name());
		// }
		Map<String, Object> value = new HashMap();
		value.put("lastModifiedDate", LocalDateTime.now());
		fundTransactService.updateField(fundTransact.getCode(), currentUserService.getCurrentUser(), "",
				"lastModifiedDate", value);

		if (!findTransact.getStatus().equals(TransactionStatus.WAITTING.name())) {
			throw new SodException(ErrorCode.RESOLVED.getReasonPhrase(), ErrorCode.RESOLVED.name());
		}

		FundTransaction result = fundTransactService.update(fundTransact);
		if (result == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(result, HttpStatus.CREATED);

	}
	
	  @RequestMapping(value = "/export-transact-tree", method = RequestMethod.POST,
		      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
		  public ResponseEntity<byte[]> exportTransactTree(@RequestParam("page") int page,
					@RequestParam("size") int size, @RequestParam(value = "query", required = false) String query,
					@RequestParam(value = "sort", required = false) String sort,
					@RequestParam(value = "direction", required = false) String direction,
					@RequestParam("countRecord") int countRecord)
		      throws SodException, IOException {
			  
			  
				// Pageable and Sort
				PageRequest request = null;

				request = PageRequest.of(0, countRecord, new Sort(Sort.Direction.DESC, "lastModifiedDate"));
				Gson gson = new Gson();
				JsonObject queryParam = gson.fromJson(query, JsonObject.class);
				String code = queryParam.get("code").getAsString();
				String objectType = queryParam.get("objectType").getAsString();
				String objectCode = queryParam.get("objectCode").getAsString();
				String fundCode = queryParam.get("fundCode").getAsString();
				String transactionType = queryParam.get("transactionType").getAsString();
				String determinantEntryCode = queryParam.get("determinantEntryCode").getAsString();
				String currency = queryParam.get("currency").getAsString();
				String status = queryParam.get("status").getAsString();
				String amountFrom = queryParam.get("amountFrom").getAsString();
				String amountTo = queryParam.get("amountTo").getAsString();
				String createdDateFrom = queryParam.get("createdDateFrom").getAsString();
				String createdDateTo = queryParam.get("createdDateTo").getAsString();
				String timeReportFrom = queryParam.get("timeReportFrom").getAsString();
				String timeReportTo = queryParam.get("timeReportTo").getAsString();
				Boolean absoluteDeter = queryParam.get("absoluteDeter").getAsBoolean();


		    PageRequest requestPage =
		        PageRequest.of(0, countRecord, new Sort(Sort.Direction.DESC, "lastModifiedDate"));
		    Map<String, Object> result =
		        fundTransactService.filterFundTransact(timeReportFrom, timeReportTo, absoluteDeter, code,
		            objectType, objectCode, fundCode, transactionType, determinantEntryCode, currency,
		            status, amountFrom, amountTo, createdDateFrom, createdDateTo, requestPage);

		    List<FundTransaction> fundTransacts = (List<FundTransaction>) result.get("data");


		    // Lấy thông tin nhân viên xử lý
		    List<String> employeeCodes = new ArrayList();
		    for (FundTransaction fundTransact : fundTransacts) {
		      if (fundTransact.getAccountantAccept() != null
		          && !employeeCodes.contains(fundTransact.getAccountantAccept())) {
		        employeeCodes.add(fundTransact.getAccountantAccept());
		      }
		      if (fundTransact.getCreatedBy() != null
		          && !employeeCodes.contains(fundTransact.getCreatedBy())) {
		        employeeCodes.add(fundTransact.getCreatedBy());
		      }
		    }

		   
		Map<String, String> mapFieldNames = new LinkedHashMap<>();
		    mapFieldNames.put("code", "Mã");
		    mapFieldNames.put("determinant", "Định khoản");
		    mapFieldNames.put("transactionType", "Loại giao dịch");
		    mapFieldNames.put("amount", "Giá trị giao dịch");
		    mapFieldNames.put("balanceBefore", "Số dư trước giao dịch");
		    mapFieldNames.put("balanceAfter", "Số dư sau giao dịch");
		    mapFieldNames.put("exchangeRate", "Tỷ giá");
		    mapFieldNames.put("format", "Đơn vị tiền tệ");
		    mapFieldNames.put("fund", "Quỹ thực hiện");
		    mapFieldNames.put("content", "Nội dung");
		    mapFieldNames.put("accountantAccept", "Người thực hiện");
		    mapFieldNames.put("status", "Trạng thái");
		    mapFieldNames.put("createdDate", "Thời gian tạo phiếu");
		    mapFieldNames.put("createdBy", "Người tạo");


		    String fileName = "FundTransaction_" + LocalDate.now().toString() + ".xlsx";
		    String filePath = properties.getPathToSave() + fileName;
		   // String filePath = "D:\\" + fileName;

		    try {
		      FunctionalUtils.exportExcel(fundTransacts, mapFieldNames, filePath,
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
