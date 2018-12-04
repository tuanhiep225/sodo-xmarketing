package com.sodo.xmarketing.utils;


import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sodo.xmarketing.dto.FundInfo;
import com.sodo.xmarketing.model.fund.Fund;
import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.model.wallet.Determinant;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.TransactionType;
import com.sodo.xmarketing.repository.fund.FundDeterminantRepository;
import com.sodo.xmarketing.repository.fund.FundRepository;
import com.sodo.xmarketing.status.SystemE;

@Component
public class FundTransactHelper {
	@Autowired
	  FundDeterminantRepository determinantRepository;
	@Autowired
	  FundRepository fundRepository;
	  public FundTransaction createAddTransact(String code, FundTransaction orgTransaction) {
		  FundTransaction result = new FundTransaction();
		    FundDeterminant fundDeterminant =
		        determinantRepository.findByCode(SystemFunDeter.TRANSFER_TARGET_FUND.getValue());
		    Fund targetFund = fundRepository.getByCode(orgTransaction.getTargetFundCode());
		    result.setCode(code);
		    result.setAmount(orgTransaction.getTargetAmount());
		    result.setContent("Giao dịch chuyển quỹ từ GD: " + orgTransaction.getCode() + ", transaction: "
		        + orgTransaction.getTransactionChainCode());
		    result.setCreatedByAccountant(false);
		    result.setFormat(targetFund.getFormat());
		    result.setDeterminant(Determinant.builder().name(fundDeterminant.getName()).code(fundDeterminant.getCode()).treeCode(fundDeterminant.getTreeCode()).build());
		    result.setFund(FundInfo.builder().code(targetFund.getCode()).name(targetFund.getName()).build());
		    result.setStatus(TransactionStatus.WAITTING.name());
		    result.setTextSearch(orgTransaction.getTextSearch() + " " + code);
		    result.setTimeReport(orgTransaction.getTimeReport());
		    result.setTransactionType(TransactionType.DEBIT.name());
		    result.setTreeCode(fundDeterminant.getTreeCode());
		    result.setTransactionChainCode(orgTransaction.getTransactionChainCode());
		    result.setExchangeRate(orgTransaction.getExchangeRate()); 
		    result.setEmployeeCreate(SystemE.SYSTEM_USERNAME.name());
		    result.setCreatedDate(LocalDateTime.now());
		    result.setLastModifiedDate(LocalDateTime.now());
		    return result;
		  }

}
