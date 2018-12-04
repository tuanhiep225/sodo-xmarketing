/**
 * 
 */
package com.sodo.xmarketing.repository.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.CustomerSearch;
import com.sodo.xmarketing.dto.EmployeeSearch;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.repository.CustomerCustomRepository;
import com.sodo.xmarketing.status.OrderStatus;
import com.sodo.xmarketing.status.Role;

/**
 * @author tuanhiep225
 *
 */
public class CustomerCustomRepositoryImpl implements CustomerCustomRepository{
	
	@Autowired
	private MongoTemplate mongoTemplate;

	private static final Log LOGGER = LogFactory.getLog(CustomerCustomRepositoryImpl.class);

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.customer.CustomerCustomRepository#filterForCMS(java.lang.String, java.lang.String, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	@Deprecated
	public SodSearchResult<Customer> filterForCMS(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser) {
		List<Criteria> criteriaList = new ArrayList<>();
		Query query = new Query();
		if (currentUser.isCustomer()) {
			return SodSearchResult.<Customer>builder().items(null).totalPages(0).totalRecord(0).build();
		} else {
				criteriaList.add(Criteria.where("isDelete").is(false));
		}

		if (!Strings.isNullOrEmpty(keyword)) {
			if (Strings.isNullOrEmpty(param) || param.equals("all")) {
				query = query.addCriteria(new Criteria()
						.orOperator(Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("email").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("phone").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("name").regex(Pattern.compile(keyword).pattern(), "i"))
						.andOperator(criteriaList.toArray(new Criteria[0])));

			} else {
				if (param.equals("username")) {
					criteriaList.add(Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("email")) {
					criteriaList.add(Criteria.where("email").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("phone")) {
					criteriaList.add(Criteria.where("phone").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("code")) {
					criteriaList.add(Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("name")) {
					criteriaList.add(Criteria.where("name").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
			}
		} else {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		List<Customer> rsTotal = mongoTemplate.find(query, Customer.class);
		query.with(pageable);
		List<Customer> rsPage = mongoTemplate.find(query, Customer.class);
		SodSearchResult<Customer> searchRS = new SodSearchResult<>();
		searchRS.setItems(rsPage);
		searchRS.setTotalRecord(rsTotal.size());
		searchRS.setTotalPages(Math.floorDiv(rsTotal.size(), pageable.getPageSize()) + 1);
		return searchRS;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.CustomerCustomRepository#suggest(java.lang.String, int)
	 */
	@Override
	public List<Customer> suggest(String queryStr, int numberRecord) throws SodException {
	    List<Customer> result = new ArrayList<>();
	    try {
	      Criteria findCustomer =  new Criteria().andOperator(Criteria.where("isDelete").is(false),  new Criteria().orOperator(
	    		  Criteria.where("name").regex(queryStr),
	    		  Criteria.where("code").regex(queryStr),
	    		  Criteria.where("email").regex(queryStr),
	    		  Criteria.where("phone").regex(queryStr),
	    		  Criteria.where("username").regex(queryStr)
	    		  ));
	      Query query = new Query();
	      query.addCriteria(findCustomer);
	      query.fields().include("name");
	      query.fields().include("code");
	      query.fields().include("email");
	      query.fields().include("phone");
	      query.fields().include("username");
	      query.fields().include("balance");
	      query.fields().include("format");
	      result = mongoTemplate.find(query.limit(numberRecord), Customer.class);
	    } catch (Exception e) {
	      throw new SodException(e.getLocalizedMessage(), e.getMessage());
	    }
	    return result;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.CustomerCustomRepository#filterV3(com.sodo.xmarketing.dto.CustomerSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Customer> filterV3(CustomerSearch customerSearch, PageRequest pageable,
			CurrentUser currentUser, Role role) {
		Query query = new Query();
		SodSearchResult<Customer> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(customerSearch);
		if(Role.ROLE_SALE.equals(role)) {
			criterias.add(Criteria.where("sale.code").is(currentUser.getCode()));
		}
		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, Customer.class));
		query.with(pageable);
		result.setItems(mongoTemplate.find(query, Customer.class));
		return result;
	}
	
	private List<Criteria> createCriteriaFilter(CustomerSearch data) {
		List<Criteria> criterias = new ArrayList<>();

		if (!Strings.isNullOrEmpty(data.getCode())) {
			criterias.add(Criteria.where("code").is(data.getCode()));
		}

		if (!Strings.isNullOrEmpty(data.getUsername())) {
			criterias.add(Criteria.where("username").is(data.getUsername()));
		}

		if (!Strings.isNullOrEmpty(data.getEmail())) {
			criterias.add(Criteria.where("email").is(data.getEmail()));
		}
		

		if (!Strings.isNullOrEmpty(data.getName())) {
			criterias.add(Criteria.where("name").is(data.getName()));
		}
		

		if (!Strings.isNullOrEmpty(data.getPhone())) {
			criterias.add(Criteria.where("phone").is(data.getPhone()));
		}
		

		if (!Strings.isNullOrEmpty(data.getLevel())) {
			criterias.add(Criteria.where("attribute").is(data.getLevel()));
		}
		
		Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
		criterias.add(criteriaIsDelete);

		return criterias;
	}

}
