/**
 * 
 */
package com.sodo.xmarketing.repository.fund.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;

import com.google.common.base.Strings;
import com.sodo.xmarketing.constants.Constants;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.fund.FundTransactCustomRepository;

/**
 * @author tuanhiep225
 *
 */
public class FundTransactRepositoryImpl implements FundTransactCustomRepository {

	@Autowired
	private MongoTemplate mongoTemplate;
	private static final Log LOGGER = LogFactory.getLog(FundTransactRepositoryImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.fund.FundTransactCustomRepository#
	 * updateFieldsFundTransact( java.lang.String, java.util.Map,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public boolean updateFieldsFundTransact(String code, Map<String, Object> mapFields, CurrentUser currentUser) {
		if (mapFields.isEmpty()) {
			return false;
		}

		Query query = new Query();
		MongoConverter mongoConverter = mongoTemplate.getConverter();

		query.addCriteria(Criteria.where("code").is(code));
		Update update = new Update();

		mapFields.forEach((key, value) -> update.set(key, mongoConverter.convertToMongoType(value)));

		if (currentUser != null && !currentUser.isCustomer()) {
			update.set("lastModifiedDate", mongoConverter.convertToMongoType(LocalDateTime.now()));
			update.set("lastModifiedBy", currentUser.getUserName());
		}
		mongoTemplate.updateFirst(query, update, FundTransaction.class);

		return true;
	}

	@Override
	public Map<String, Object> filterFundTransact(String timeReportFrom, String timeReportTo, Boolean absoluteDeter,
			String code, String objectType, String objectCode, String fundCode, String transactionType,
			String determinantEntryCode, String currency, String status, String amountFrom, String amountTo,
			String createdDateFrom, String createdDateTo, PageRequest request) throws SodException {

		Map<String, Object> result = new HashMap<>();
		List<FundTransaction> listFundTransact;
		Query query = new Query();

		List<Criteria> criterias = new ArrayList<>();
		Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
		criterias.add(criteriaIsDelete);

		// 1 code
		if (!Strings.isNullOrEmpty(code)) {
			Criteria codeCriteria = Criteria.where("code").is(code);
			criterias.add(codeCriteria);
		}
		// 2 objectType
		if (!Strings.isNullOrEmpty(objectType)) {
			Criteria objectTypeCriteria = Criteria.where("objectType").is(objectType);
			criterias.add(objectTypeCriteria);
		}
		// 3 objectType
		if (!Strings.isNullOrEmpty(objectCode)) {
			Criteria objectCodeCriteria = Criteria.where("objectCode").is(objectCode);
			criterias.add(objectCodeCriteria);
		}

		// 4 fundCode
		if (!Strings.isNullOrEmpty(fundCode)) {
			Criteria fundCodeCriteria = Criteria.where("fund.code").is(fundCode);
			criterias.add(fundCodeCriteria);
		}
		// 5 transactionType
		if (!Strings.isNullOrEmpty(transactionType)) {
			Criteria transactionTypeCriteria = Criteria.where("transactionType").is(transactionType);
			criterias.add(transactionTypeCriteria);
		}
		// 6 determinantEntryCode
		if (!Strings.isNullOrEmpty(determinantEntryCode)) {
			String querrrry = determinantEntryCode.replace(".", "\\.");
			Pattern r = Pattern.compile("^" + querrrry);
			Criteria startWithTreeCode = Criteria.where("treeCode").regex(r);
			Criteria isTreeCode = Criteria.where("treeCode").is(determinantEntryCode);
			Criteria filterAndOwner = new Criteria().orOperator(isTreeCode, startWithTreeCode);
			if (absoluteDeter) {
				criterias.add(isTreeCode);
			} else {
				criterias.add(filterAndOwner);
			}
		}
		// 7 currency
		if (!Strings.isNullOrEmpty(currency)) {
			Criteria currencyCriteria = Criteria.where("format.currency").is(currency);
			criterias.add(currencyCriteria);
		}

		// 8 status
		if (!Strings.isNullOrEmpty(status)) {
			Criteria statusCriteria = Criteria.where("status").is(status);
			criterias.add(statusCriteria);
		}

		// 9 amountFrom
		if (!Strings.isNullOrEmpty(amountFrom)) {
			BigDecimal amountFromBigDcm = new BigDecimal(amountFrom);
			Criteria amount = Criteria.where("amount").gte(amountFromBigDcm);
			criterias.add(amount);
		}
		// 10 amountTo
		if (!Strings.isNullOrEmpty(amountTo)) {
			BigDecimal amountToBigDcm = new BigDecimal(amountTo);
			Criteria amount = Criteria.where("amount").lte(amountToBigDcm);
			criterias.add(amount);
		}

		// 11 createdate
		if (!Strings.isNullOrEmpty(createdDateFrom)) {
			LocalDateTime parseStartDateTime = LocalDateTime.parse(createdDateFrom + " 00:00:00",
					Constants.Format.DATE_TIME_FORMAT);
			Criteria criteriaDate = Criteria.where("createdDate").gte(parseStartDateTime);
			criterias.add(criteriaDate);
		}
		// 12 createdate
		if (!Strings.isNullOrEmpty(createdDateTo)) {
			LocalDateTime parseStartDateTime = LocalDateTime.parse(createdDateTo + " 23:59:59",
					Constants.Format.DATE_TIME_FORMAT);
			Criteria criteriaDate = Criteria.where("createdDate").lte(parseStartDateTime);
			criterias.add(criteriaDate);
		}
		// 13 timeReportFrom
		if (!Strings.isNullOrEmpty(timeReportFrom)) {
			LocalDateTime parseStartDateTime = LocalDateTime.parse(timeReportFrom + " 00:00:00",
					Constants.Format.DATE_TIME_FORMAT);
			Criteria criteriaTimeReportFrom = Criteria.where("timeReport").gte(parseStartDateTime);
			criterias.add(criteriaTimeReportFrom);
		}
		// 14 timeReportTo
		if (!Strings.isNullOrEmpty(timeReportTo)) {
			LocalDateTime parseStartDateTime = LocalDateTime.parse(timeReportTo + " 23:59:59",
					Constants.Format.DATE_TIME_FORMAT);
			Criteria criteriaTimeReportTo = Criteria.where("timeReport").lte(parseStartDateTime);
			criterias.add(criteriaTimeReportTo);
		}

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}
		Long count = mongoTemplate.count(query, FundTransaction.class);
		result.put("count", count);
		query.with(request);
		try {
			listFundTransact = mongoTemplate.find(query, FundTransaction.class);
			result.put("data", listFundTransact);
		} catch (Exception e) {
			throw new SodException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());

		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.fund.FundTransactCustomRepository#updateField(
	 * java.lang.String, com.sodo.xmarketing.model.account.CurrentUser,
	 * java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public FundTransaction updateField(String code, CurrentUser currentUser, String action, String fieldName,
			Map<String, Object> value) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		Update update = new Update();
		update.set(fieldName, value.get(fieldName));
		mongoTemplate.updateFirst(query, update, WalletTransaction.class);
		return mongoTemplate.findOne(query, FundTransaction.class);
	}
	
	  @Override
	  public void deleteOrCancelTransactions(String action, List<String> values,
	      CurrentUser currentUser) {
	    Query query = new Query().addCriteria(Criteria.where("code").in(values));
	    Update update = new Update();
	    if (action.equalsIgnoreCase("delete")) {
	      update.set("isDelete", true);
	    }
	    if (action.equalsIgnoreCase("cancel")) {
	      update.set("status", TransactionStatus.CANCEL.name());
	    }
	    mongoTemplate.updateMulti(query, update, FundTransaction.class);

	  }
	  
	  @Override
	  public FundTransaction getByFieldName(String fieldName, String value) {
	    Query queryCri = new Query();
	    List<Criteria> criterias = new ArrayList<>();
	    // Criteria deleteCriteria = Criteria.where("isDelete").is(false);
	    Criteria querryCriteria = Criteria.where(fieldName).is(value);
	    criterias.add(querryCriteria);
	    // criterias.add(deleteCriteria);

	    if (!criterias.isEmpty()) {
	      Criteria[] andCriteriaAray = new Criteria[criterias.size()];
	      queryCri.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
	    }

	    return mongoTemplate.findOne(queryCri, FundTransaction.class);

	  }
}
