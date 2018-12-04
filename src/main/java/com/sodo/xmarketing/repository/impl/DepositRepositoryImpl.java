/**
 * 
 */
package com.sodo.xmarketing.repository.impl;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.base.Strings;
import com.sodo.xmarketing.constants.Constants;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.account.Deposit;
import com.sodo.xmarketing.repository.DepositCustomRepository;

/**
 * @author tuanhiep225
 *
 */
public class DepositRepositoryImpl implements DepositCustomRepository {

	@Autowired
	private MongoTemplate mongoTemplate;
	private static final Log LOGGER = LogFactory.getLog(DepositRepositoryImpl.class);
	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.DepositCustomRepository#getDepositByFilter(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Map<String, Object> getDepositByFilter(String customerAccount, String staffCode, String senderName, String cardNumber, String startDate,
			String endDate, String status, Pageable pageable) throws SodException {

	    Map<String, Object> result = new HashMap<>();

	    // ----------------------
	    List<Deposit> deposit = null;
	    Query query = new Query();
	    Criteria criterCustomerAccount;
	    Criteria criterEmployeeAccount;
	    Criteria criterSenderName;
	    Criteria criterCardNumber;
	    Criteria criterStatus;

	    List<Criteria> criterias = new ArrayList<>();

	    if (!Strings.isNullOrEmpty(customerAccount)) {
	      criterCustomerAccount = Criteria.where("userName").is(customerAccount);
	      criterias.add(criterCustomerAccount);
	    }
	    if (!Strings.isNullOrEmpty(senderName)) {
	    	criterSenderName = Criteria.where("senderName").is(senderName);
		      criterias.add(criterSenderName);
		    }
	    if (!Strings.isNullOrEmpty(cardNumber)) {
	    	criterCardNumber = Criteria.where("cardNumber").is(cardNumber);
		      criterias.add(criterCardNumber);
		    }
	    if (!Strings.isNullOrEmpty(staffCode)) {
	      criterEmployeeAccount = Criteria.where("staffHandle.code").is(staffCode);
	      criterias.add(criterEmployeeAccount);
	    }
	    if (!Strings.isNullOrEmpty(status)) {
	      criterStatus = Criteria.where("status").is(status);
	      criterias.add(criterStatus);
	    }

	    // 11 createdate
	    if (!Strings.isNullOrEmpty(startDate)) {
	      LocalDateTime parseStartDate = LocalDateTime.parse(startDate + " 00:00:01", Constants.Format.DATE_TIME_FORMAT);
	      Criteria criteriaDate = Criteria.where("createdDate").gte(parseStartDate);
	      criterias.add(criteriaDate);
	    }
	    // 12 createdate
	    if (!Strings.isNullOrEmpty(endDate)) {
	      LocalDateTime parseEndDate = LocalDateTime.parse(endDate + " 23:59:59", Constants.Format.DATE_TIME_FORMAT);
	      Criteria criteriaDate = Criteria.where("createdDate").lte(parseEndDate);
	      criterias.add(criteriaDate);
	    }

	    Criteria criteriaIsdelete = Criteria.where("isDelete").is(false);
	    criterias.add(criteriaIsdelete);
	    if (!criterias.isEmpty()) {
	      Criteria[] andCriteriaAray = new Criteria[criterias.size()];
	      query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
	    }
	    Long countRecord = mongoTemplate.count(query, Deposit.class);
	    result.put("count", countRecord);
	    query.with(pageable);
	    try {
	      deposit = mongoTemplate.find(query, Deposit.class);
	    } catch (Exception e) {
	      throw new SodException(e.getLocalizedMessage(), e.getMessage());
	    }
	    result.put("deposits", deposit);

	    return result;
	}
	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.DepositCustomRepository#updateField(java.lang.String, com.sodo.xmarketing.model.account.CurrentUser, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public Deposit updateField(String code, CurrentUser currentUser, String action, String fieldName,
			Map<String, Object> value) {
	    Query query = new Query();
	    query.addCriteria(Criteria.where("code").is(code));
	    Update update = new Update();
	    update.set(fieldName, value.get(fieldName));
	    mongoTemplate.updateFirst(query, update, Deposit.class);
	    return mongoTemplate.findOne(query, Deposit.class);
	}
	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.DepositCustomRepository#acceptOrRefuse(java.lang.String, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Deposit acceptOrRefuse(String code, String status, CurrentUser currentUser) {
		Query query = new Query();
	    query.addCriteria(Criteria.where("code").is(code).and("isDelete").is(false));
	    Update update = new Update();
	    update.set("status", status);
	    update.set("acceptDate", LocalDateTime.now());
	    
	    StaffDTO staff = StaffDTO.builder().code(currentUser.getCode()).email(currentUser.getEmail()).name(currentUser.getFullName()).username(currentUser.getUserName()).build();
	    update.set("staffHandle", staff);
	    update.set("lastModifiedDate", LocalDateTime.now());
	    update.set("lastModifiedBy", currentUser.getCode());
	    return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), Deposit.class);
	}
}
