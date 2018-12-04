/**
 * 
 */
package com.sodo.xmarketing.repository.wallet.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.AccountingEntryFilter;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.wallet.WalletTransactionCustomRepository;

/**
 * @author tuanhiep225
 *
 */
public class WalletTransactionRepositoryImpl implements WalletTransactionCustomRepository {

	private static final Log LOGGER = LogFactory.getLog(WalletTransactionRepositoryImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	private static final String END_DATETIME_PLUS = " 23:59:59";
	private static final String START_DATETIME_PLUS = " 00:00:01";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.wallet.WalletTransactionCustomRepository#
	 * filterAccounting(com.sodo.xmarketing.dto.AccountingEntryFilter, int, int)
	 */
	@Override
	public SodSearchResult<WalletTransaction> filterAccounting(AccountingEntryFilter data, int page, int size) {
		Query query = new Query();
		PageRequest request = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "createdDate"));
		SodSearchResult<WalletTransaction> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(data);

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, WalletTransaction.class));
		query.with(request);
		result.setItems(mongoTemplate.find(query, WalletTransaction.class));
		return result;
	}

	/**
	 * @author anhmi - tạo query filter
	 * @param data
	 * @return
	 */
	private List<Criteria> createCriteriaFilter(AccountingEntryFilter data) {
		Criteria criteriaUserName;
		Criteria criteriaTransactionType;
		Criteria criteriaVoucherCode;
		Criteria criteriaAmount;
		Criteria criteriaStatus;
		Criteria criteriaCreatedDate = Criteria.where("createdDate");
		List<Criteria> criterias = new ArrayList<>();

		if (!Strings.isNullOrEmpty(data.getUserName())) {
			criteriaUserName = Criteria.where("wallet.customerUserName").is(data.getUserName());
			criterias.add(criteriaUserName);
		}

		if (!Strings.isNullOrEmpty(data.getStartDate())) {
			String startDate = data.getStartDate() + START_DATETIME_PLUS;
			LocalDateTime parseStartDateTime = LocalDateTime.parse(startDate,
					DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
			criteriaCreatedDate = criteriaCreatedDate.gte(parseStartDateTime);
			criterias.add(criteriaCreatedDate);

		}
		if (!Strings.isNullOrEmpty(data.getEndDate())) {
			String endDate = data.getEndDate() + END_DATETIME_PLUS;
			LocalDateTime parseEndDateTime = LocalDateTime.parse(endDate,
					DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
			criteriaCreatedDate = criteriaCreatedDate.lte(parseEndDateTime);
			criterias.add(criteriaCreatedDate);
		}
		if (!Strings.isNullOrEmpty(data.getWalletDeterminantCode())) {
			Criteria deterCriteria = addDeterCriteria(data.getWalletDeterminantCode(), data.getAbsoluteDeter());
			criterias.add(deterCriteria);
		}

		if (!Strings.isNullOrEmpty(data.getTransactionType())) {
			criteriaTransactionType = Criteria.where("type").is(data.getTransactionType());
			criterias.add(criteriaTransactionType);
		}

		if (!Strings.isNullOrEmpty(data.getCode())) {
			criteriaVoucherCode = Criteria.where("code").is(data.getCode());
			criterias.add(criteriaVoucherCode);
		}

		if (data.getAmountFrom() != null && data.getAmountTo() == null) {
			criteriaAmount = Criteria.where("amount")
					.gte(data.getAmountFrom());
			criterias.add(criteriaAmount);
		}
		if (data.getAmountFrom() != null && data.getAmountTo() != null) {
			criteriaAmount = Criteria.where("amount")
					.gte(data.getAmountFrom()).lte(data.getAmountTo());
			criterias.add(criteriaAmount);
		}
		if (data.getAmountFrom() == null && data.getAmountTo() != null) {
			criteriaAmount = Criteria.where("amount")
					.lte(data.getAmountTo());
			criterias.add(criteriaAmount);
		}
		if (!Strings.isNullOrEmpty(data.getStatus())) {
			criteriaStatus = Criteria.where("status").is(data.getStatus());
			criterias.add(criteriaStatus);
		}
		if (!Strings.isNullOrEmpty(data.getOrderCode())) {
			Criteria criteriaOrderCode = Criteria.where("target.code").is(data.getOrderCode()).and("target.type").is("ORDER");
			criterias.add(criteriaOrderCode);
		}

		Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
		criterias.add(criteriaIsDelete);

		return criterias;
	}

	/**
	 * @author anhmi - xử lý treeCode
	 * @param deter
	 * @param absoluteDeter
	 * @return
	 */
	private Criteria addDeterCriteria(String deter, Boolean absoluteDeter) {

		String afterReplaceString = deter.replace(".", "\\.");
		Pattern r = Pattern.compile("^" + afterReplaceString);
		Criteria startWithTreeCode = Criteria.where("walletDeterminant.treeCode").regex(r);
		Criteria isTreeCode = Criteria.where("walletDeterminant.treeCode").is(deter);
		Criteria filterAndOwner = new Criteria().orOperator(isTreeCode, startWithTreeCode);
		// lọc ra theo 1 định khoản
		if (absoluteDeter) {
			return isTreeCode;
		} else {
			return filterAndOwner;
		}
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.wallet.WalletTransactionCustomRepository#suggestAccountingEntryCode(java.lang.String, int)
	 */
	@Override
	public List<WalletTransaction> suggestAccountingEntryCode(String querry, int numberRecord) {
	    Criteria findOrderMatch =
	            Criteria.where("code").regex(Pattern.compile(querry, Pattern.CASE_INSENSITIVE));
	        Query query = new Query();
	        query.addCriteria(findOrderMatch);
	        query.fields().include("code");

	        return mongoTemplate.find(query.limit(numberRecord), WalletTransaction.class);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.wallet.WalletTransactionCustomRepository#updateField(java.lang.String, com.sodo.xmarketing.model.account.CurrentUser, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public WalletTransaction updateField(String code, CurrentUser currentUser, String action, String fieldName,
			Map<String, Object> value) {
	    Query query = new Query();
	    query.addCriteria(Criteria.where("code").is(code));
	    Update update = new Update();
	    update.set(fieldName, value.get(fieldName));
	    mongoTemplate.updateFirst(query, update, WalletTransaction.class);
	    return mongoTemplate.findOne(query, WalletTransaction.class);
	}
}
