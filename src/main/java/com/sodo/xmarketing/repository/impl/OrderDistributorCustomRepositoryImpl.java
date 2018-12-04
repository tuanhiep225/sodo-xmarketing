/**
 * 
 */
package com.sodo.xmarketing.repository.impl;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.OrderDistributorSearch;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.OrderDistributor;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.wallet.TransactionStatus;
import com.sodo.xmarketing.repository.OrderDistributorCustomRepository;

/**
 * @author tuanhiep225
 *
 */
public class OrderDistributorCustomRepositoryImpl implements OrderDistributorCustomRepository{

	private static final Log LOGGER = LogFactory.getLog(OrderDistributorCustomRepositoryImpl.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String END_DATETIME_PLUS = " 23:59:59";
	private static final String START_DATETIME_PLUS = " 00:00:01";
	
	private List<Criteria> createCriteriaFilter(OrderDistributorSearch data) {
		Criteria criteriaAmount;
		Criteria criteriaCreatedDate = Criteria.where("createdDate");
		List<Criteria> criterias = new ArrayList<>();

		if (!Strings.isNullOrEmpty(data.getCode())) {
			criterias.add(Criteria.where("code").is(data.getCode()));
		}
		
		if (!Strings.isNullOrEmpty(data.getOrderCode())) {
			criterias.add(Criteria.where("orderCode").is(data.getOrderCode()));
		}

		if (!Strings.isNullOrEmpty(data.getUsername())) {
			criterias.add(Criteria.where("username").is(data.getUsername()));
		}

		if (!Strings.isNullOrEmpty(data.getStaffCode())) {
			criterias.add(Criteria.where("staff.code").is(data.getStaffCode()));
		}
		
		if (!Strings.isNullOrEmpty(data.getDistributorCode())) {
			criterias.add(Criteria.where("distributor").is(data.getDistributorCode()));
		}

		if (data.getFromValue() != null && data.getToValue() == null) {
			criteriaAmount = Criteria.where("price")
					.gte(data.getFromValue());
			criterias.add(criteriaAmount);
		}
		if (data.getFromValue() != null && data.getToValue() != null) {
			criteriaAmount = Criteria.where("price")
					.gte(data.getFromValue()).lte(data.getToValue());
			criterias.add(criteriaAmount);
		}
		if (data.getFromValue() == null && data.getToValue() != null) {
			criteriaAmount = Criteria.where("price")
					.lte(data.getToValue());
			criterias.add(criteriaAmount);
		}
		if (!Strings.isNullOrEmpty(data.getStatus())) {
			criterias.add(Criteria.where("status").is(data.getStatus()));
		}
		if (!Strings.isNullOrEmpty(data.getTransactionStatus())) {
			criterias.add(Criteria.where("transactionStatus").is(data.getTransactionStatus()));
		}

		Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
		criterias.add(criteriaIsDelete);
		criterias.add(Criteria.where("isRequestPayment").is(true));

		return criterias;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.OrderDistributorCustomRepository#filterForMyOrderV3(com.sodo.xmarketing.dto.OrderDistributorSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<OrderDistributor> filter(OrderDistributorSearch orderSearch,
			PageRequest pageable, CurrentUser currentUser) {
		
		Query query = new Query();
		SodSearchResult<OrderDistributor> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(orderSearch);

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, OrderDistributor.class));
		query.with(pageable);
		result.setItems(mongoTemplate.find(query, OrderDistributor.class));
		return result;
	}
}
