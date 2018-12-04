/**
 * 
 */
package com.sodo.xmarketing.repository.employee.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.sodo.xmarketing.dto.EmployeeSearch;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.customer.Customer;
import com.sodo.xmarketing.model.employee.Employee;
import com.sodo.xmarketing.repository.employee.EmployeeCustomRepository;
import com.sodo.xmarketing.status.CustomerStatus;
import com.sodo.xmarketing.status.Role;
import lombok.var;

/**
 * @author tuanhiep225
 *
 */
public class EmployeeRepositoryImpl implements EmployeeCustomRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	private static final Log LOGGER = LogFactory.getLog(EmployeeRepositoryImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.employee.EmployeeCustomRepository#changeVIP(java.lang.String,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> changeVIP(String customerCode, CustomerStatus attribute,
			CurrentUser currentUser) {
		Query query = new Query();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code").is(customerCode));
		Update update = new Update();
		update.set("attribute", attribute);
		try {
			var rs = mongoTemplate.updateFirst(query, update, Customer.class);
			return SodResult.<Boolean>builder().result(rs.isModifiedCountAvailable()).build();
		} catch (Exception e) {
			return SodResult.<Boolean>builder().isError(true).message(e.getMessage()).build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.employee.EmployeeCustomRepository#filter(java.lang.String,
	 * java.lang.String, org.springframework.data.domain.PageRequest,
	 * com.sodo.xmarketing.model.account.CurrentUser, java.lang.String)
	 */
	@Override
	public SodSearchResult<Employee> filter(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser, String role) {
		List<Criteria> criteriaList = new ArrayList<>();
		Query query = new Query();
		if (currentUser.isCustomer()) {
			return SodSearchResult.<Employee>builder().items(null).totalPages(0).totalRecord(0)
					.build();
		} else {
			if (!(currentUser.getRoles().contains(Role.ROLE_ADMIN.toString())
					|| currentUser.getRoles().contains(Role.ROLE_MANAGER.toString()))) {
				return SodSearchResult.<Employee>builder().items(null).totalPages(0).totalRecord(0)
						.build();
			}
			criteriaList.add(Criteria.where("isDelete").is(false));
			// if(Role.ROLE_ADMIN.toString().equals(role))
			// criteriaList.add(Criteria.where("isDelete").is(false));
			// else
			// return
			// SodSearchResult.<Employee>builder().items(null).totalPages(0).totalRecord(0).build();
		}

		if (!Strings.isNullOrEmpty(keyword)) {
			if (Strings.isNullOrEmpty(param) || param.equals("all")) {
				query = query.addCriteria(new Criteria().orOperator(
						Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"),
						Criteria.where("email").regex(Pattern.compile(keyword).pattern(), "i"),
						Criteria.where("phone").regex(Pattern.compile(keyword).pattern(), "i"),
						Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"),
						Criteria.where("name").regex(Pattern.compile(keyword).pattern(), "i"))
						.andOperator(criteriaList.toArray(new Criteria[0])));

			} else {
				if (param.equals("username")) {
					criteriaList.add(Criteria.where("username")
							.regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("email")) {
					criteriaList.add(
							Criteria.where("email").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("phone")) {
					criteriaList.add(
							Criteria.where("phone").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("code")) {
					criteriaList.add(
							Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("name")) {
					criteriaList.add(
							Criteria.where("name").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				query.addCriteria(
						new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
			}
		} else {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		List<Employee> rsTotal = mongoTemplate.find(query, Employee.class);
		query.with(pageable);
		List<Employee> rsPage = mongoTemplate.find(query, Employee.class);
		SodSearchResult<Employee> searchRS = new SodSearchResult<>();
		searchRS.setItems(rsPage);
		searchRS.setTotalRecord(rsTotal.size());
		searchRS.setTotalPages(Math.floorDiv(rsTotal.size(), pageable.getPageSize()) + 1);
		return searchRS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.employee.EmployeeCustomRepository#suggest(java.lang.String,
	 * int)
	 */
	@Override
	public List<Employee> suggest(String queryStr, String role, int numberRecord)
			throws SodException {
		List<Employee> result = new ArrayList<>();
		try {
			Criteria findEmployee;
			if (role != null) {
				findEmployee = new Criteria().andOperator(Criteria.where("isDelete").is(false),
						Criteria.where("roles").all(role),
						new Criteria().orOperator(Criteria.where("name").regex(queryStr),
								Criteria.where("code").regex(queryStr),
								Criteria.where("email").regex(queryStr),
								Criteria.where("phone").regex(queryStr),
								Criteria.where("username").regex(queryStr)));
			} else {
				findEmployee = new Criteria().andOperator(Criteria.where("isDelete").is(false),
						new Criteria().orOperator(Criteria.where("name").regex(queryStr),
								Criteria.where("code").regex(queryStr),
								Criteria.where("email").regex(queryStr),
								Criteria.where("phone").regex(queryStr),
								Criteria.where("username").regex(queryStr)));
			}

			Query query = new Query();
			query.addCriteria(findEmployee);
			query.fields().include("name");
			query.fields().include("code");
			query.fields().include("email");
			query.fields().include("phone");
			query.fields().include("username");
			result = mongoTemplate.find(query.limit(numberRecord), Employee.class);
		} catch (Exception e) {
			throw new SodException(e.getLocalizedMessage(), e.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.employee.EmployeeCustomRepository#assign(com.sodo.xmarketing.
	 * dto.StaffDTO, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Customer assignSaleToCustomer(StaffDTO employee, String customerCode,
			CurrentUser currentUser) {
		Query query = new Query();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code").is(customerCode));
		Update update = new Update();
		update.set("sale", employee);
		return mongoTemplate.findAndModify(query, update,
				FindAndModifyOptions.options().returnNew(true), Customer.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.employee.EmployeeCustomRepository#assignSaleToOrder(com.sodo.
	 * xmarketing.dto.StaffDTO, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Boolean assignSaleToOrder(StaffDTO employee, String username, CurrentUser currentUser) {
		Query query = new Query();
		query.addCriteria(Criteria.where("isDelete").is(false).and("username").is(username)
				.and("salse").is(null));
		Update update = new Update();
		update.set("sale", employee);
		return mongoTemplate.updateMulti(query, update, Order.class).isModifiedCountAvailable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.employee.EmployeeCustomRepository#filterV3(com.sodo.xmarketing
	 * .dto.EmployeeSearch, org.springframework.data.domain.PageRequest,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Employee> filterV3(EmployeeSearch employeeSearch, PageRequest pageable,
			CurrentUser currentUser) {
		Query query = new Query();
		SodSearchResult<Employee> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(employeeSearch);

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, Employee.class));
		query.with(pageable);
		result.setItems(mongoTemplate.find(query, Employee.class));
		return result;
	}


	private List<Criteria> createCriteriaFilter(EmployeeSearch data) {
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


		if (!CollectionUtils.isEmpty(data.getRoles())) {
			criterias.add(Criteria.where("roles").all(data.getRoles()));
		}

		Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
		criterias.add(criteriaIsDelete);

		return criterias;
	}


}
