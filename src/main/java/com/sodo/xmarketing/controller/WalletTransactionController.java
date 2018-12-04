package com.sodo.xmarketing.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.sodo.xmarketing.auth.CurrentUserService;
import com.sodo.xmarketing.dto.AccountingEntryFilter;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.service.DepositService;
import com.sodo.xmarketing.service.FundTransactService;
import com.sodo.xmarketing.service.WalletTransactionService;
import com.sodo.xmarketing.utils.AccFunctionUtil;
import com.sodo.xmarketing.utils.FunctionalUtils;
import com.sodo.xmarketing.utils.Properties;

import io.swagger.annotations.ApiOperation;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 11/16
 */
@RestController
@RequestMapping("/api/v1/wallet-transaction")
public class WalletTransactionController {

	private final WalletTransactionService service;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private FundTransactService fundTransactService;
	
	@Autowired
	private DepositService depositService;
	
	@Autowired
	Properties properties;

	@Autowired
	public WalletTransactionController(WalletTransactionService service) {
		this.service = service;
	}

	/**
	 * @Henry Lấy ra các giao dịch
	 */
	@GetMapping("/wallet/{username}")
	public SodSearchResult<WalletTransaction> findByWallet(@PathVariable String username,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
		return service.findAllByWallet(username, pageIndex, pageSize);
	}

	@GetMapping("/init-data")
	public Boolean initData() throws SodException {
		service.initData(currentUserService.getCurrentUser());
		service.initAccountEntryContent();
		return true;
	}

	@RequestMapping(value = "/filter", method = RequestMethod.POST)
	public ResponseEntity<SodSearchResult<WalletTransaction>> filterAccounting(@RequestBody AccountingEntryFilter data,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		SodSearchResult<WalletTransaction> result = service.filterAccounting(data, page, size);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/suggest-accounting-entry-code", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<WalletTransaction>> suggestOrderDistributor(
			@RequestParam(value = "query", required = false) String querry,
			@RequestParam(value = "numberRecord", required = false) int numberRecord) {

		List<WalletTransaction> result = service.suggestAccountingEntryCode(querry.toUpperCase(), numberRecord);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Tạo 1 giao dịch ví
	 */
	@ApiOperation(value = "Tạo phiếu giao dịch ví")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public SodResult<WalletTransaction> createAccountingEntry(@RequestBody WalletTransaction accountingEntry)
			throws SodException {
		WalletTransaction handleBeforeCreateEntity = service.handleBeforeCreate(accountingEntry,
				currentUserService.getCurrentUser());
		SodResult<WalletTransaction> createdAccountingEntry = service.create(handleBeforeCreateEntity,
				currentUserService.getCurrentUser());
		if (createdAccountingEntry == null) {
			throw new SodException("Create accounting failed!", "CREATE");
		}
		return createdAccountingEntry;
	}

	@ApiOperation(value = "@Killer xác nhận giao dịch ví")
	@RequestMapping(value = "/accept-payment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WalletTransaction> acceptPayment(@RequestBody WalletTransaction accountingEntry)
			throws SodException {
		WalletTransaction finalAccounting = service.acceptPayment(accountingEntry, currentUserService.getCurrentUser());

		// Nếu có giao dịch quỹ đi kèm thì thực hiện giao dịch quỹ
		if (accountingEntry.getFundTransact() != null) {
			FundTransaction createdFundTransact = new AccFunctionUtil().generateAcceptPaymentObject(accountingEntry);
			fundTransactService.create(createdFundTransact, currentUserService.getCurrentUser());
			fundTransactService.acceptPayment(createdFundTransact, currentUserService.getCurrentUser());
		}

		return new ResponseEntity<>(finalAccounting, HttpStatus.OK);

	}

	@CrossOrigin
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WalletTransaction> update(@RequestBody WalletTransaction accountingEntry)
			throws SodException {

		WalletTransaction updatedAccountingEntry = service.update(accountingEntry, currentUserService.getCurrentUser());

		if (updatedAccountingEntry == null) {
			throw new SodException("Update accounting entry failed!", "UPDATE");
		}

		return new ResponseEntity<>(updatedAccountingEntry, HttpStatus.OK);
	}

	@RequestMapping(value = "/create-and-accept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WalletTransaction> createAndAccept(@RequestBody WalletTransaction accountingEntry)
			throws SodException {
		WalletTransaction handleBeforeCreateEntity = service.handleBeforeCreate(accountingEntry,
				currentUserService.getCurrentUser());
		SodResult<WalletTransaction> createdAccountingEntry = service.create(handleBeforeCreateEntity,
				currentUserService.getCurrentUser());
		WalletTransaction acceptedTransaction = service.acceptPayment(createdAccountingEntry.getResult(),
				currentUserService.getCurrentUser());

		if (acceptedTransaction == null
				||  TransactionStatus.COMPLETED.name().equals(acceptedTransaction.getStatus())) {
			throw new SodException("cannot created and accept", "cannot created and accept");
		}
		// Nếu có giao dịch quỹ đi kèm thì thực hiện giao dịch quỹ
		if (accountingEntry.getFundTransact() != null) {
			FundTransaction createdFundTransact = new AccFunctionUtil().generateAcceptPaymentObject(accountingEntry);
			fundTransactService.create(createdFundTransact, currentUserService.getCurrentUser());
			fundTransactService.acceptPayment(createdFundTransact, currentUserService.getCurrentUser());
		}
		if(!Strings.isNullOrEmpty(accountingEntry.getDepositCode())) {
			depositService.acceptOrRefuse(accountingEntry.getDepositCode(), TransactionStatus.COMPLETED.name(), currentUserService.getCurrentUser());
		}
		return new ResponseEntity<>(acceptedTransaction, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/export", method = RequestMethod.POST,produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public  ResponseEntity<byte[]> exportAccounting(@RequestBody AccountingEntryFilter data,
			@RequestParam("page") int page, @RequestParam("size") int size) throws SodException, IOException {
		SodSearchResult<WalletTransaction> result = service.filterAccounting(data, page, size);
		List<WalletTransaction> walletTransacts = (List<WalletTransaction>) result.getItems();
		
		Map<String, String> mapFieldNames = new LinkedHashMap<>();
	    mapFieldNames.put("code", "Mã phiếu");
	    mapFieldNames.put("walletDeterminant", "Định khoản");
	    mapFieldNames.put("type", "Loại giao dịch");
	    mapFieldNames.put("wallet", "Mã khách hàng");
	    mapFieldNames.put("amount", "Giá trị giao dịch");
	    mapFieldNames.put("beforeAmount", "Số dư trước giao dịch");
	    mapFieldNames.put("afterAmount", "Số dư sau giao dịch");
	    mapFieldNames.put("content", "Nội dung");
	    mapFieldNames.put("format", "Đơn vị tiền tệ");
	    mapFieldNames.put("accountantAccept", "Người thực hiện");
	    mapFieldNames.put("status", "Trạng thái");
	    mapFieldNames.put("createdDate", "Thời gian tạo phiếu");


	    String fileName = "WalletTransation" + LocalDate.now().toString() + ".xlsx";
	    String filePath = properties.getPathToSave() + fileName;
	   // String filePath = "D:\\" + fileName;

	    try {
	      FunctionalUtils.exportExcel(walletTransacts, mapFieldNames, filePath,
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
