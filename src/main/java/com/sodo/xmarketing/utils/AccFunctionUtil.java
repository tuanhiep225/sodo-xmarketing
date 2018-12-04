package com.sodo.xmarketing.utils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.sodo.xmarketing.dto.FundInfo;
import com.sodo.xmarketing.model.fund.Fund;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.model.wallet.TargetObject;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.WalletTransaction;

public class AccFunctionUtil {

  private MongoTemplate mongoTemplate;

  public AccFunctionUtil(MongoTemplate mongoTemplate) {

    this.mongoTemplate = mongoTemplate;
  }

  public AccFunctionUtil() {


  }
  public FundTransaction generateAcceptPaymentObject(WalletTransaction accountingEntry) {
	  FundTransaction createdFundTransact = new FundTransaction();
	  FundInfo fund = FundInfo.builder()
			  .code(accountingEntry.getFundTransact().getFundCode())
			  .name(accountingEntry.getFundTransact().getFundName())
			  .build();
	    createdFundTransact.setFund(fund);
	    createdFundTransact
	        .setDeterminant(accountingEntry.getFundTransact().getDeterminant());
	    createdFundTransact.setFormat(accountingEntry.getFundTransact().getFormat());
	    createdFundTransact.setExchangeRate(accountingEntry.getFundTransact().getExchangeRate());
	    createdFundTransact.setAmount(accountingEntry.getAmount());
	    createdFundTransact.setContent(accountingEntry.getFundTransact().getContent());
	    createdFundTransact.setTransactionType(accountingEntry.getType().name());
	    createdFundTransact.setStatus(TransactionStatus.WAITTING.name());
	    createdFundTransact.setCreatedByAccountant(false);
	    createdFundTransact.setTarget(accountingEntry.getTarget());
	    createdFundTransact.setEmployeeCreate(accountingEntry.getFundTransact().getEmployeeCreate());
	    return createdFundTransact;
	  }

}
