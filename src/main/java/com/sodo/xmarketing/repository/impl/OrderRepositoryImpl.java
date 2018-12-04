/**
 * 
 */
package com.sodo.xmarketing.repository.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.BsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.OrderDTO;
import com.sodo.xmarketing.dto.OrderSearch;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.Order;
import com.sodo.xmarketing.model.OrderDistributor;
import com.sodo.xmarketing.model.OrderExcel;
import com.sodo.xmarketing.model.OrderWithNCC;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.repository.OrderCustomRepository;
import com.sodo.xmarketing.status.OrderStatus;
import com.sodo.xmarketing.status.Role;

import lombok.var;

/**
 * @author tuanhiep225
 *
 */
public class OrderRepositoryImpl implements OrderCustomRepository {

	private static final Log LOGGER = LogFactory.getLog(OrderRepositoryImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String END_DATETIME_PLUS = " 23:59:59";
	private static final String START_DATETIME_PLUS = " 00:00:01";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.OrderCustomRepository#groupByStatusAndCount()
	 */
	@Override
	/**
	 * @author tuanhiep225
	 **/
	public Map<String, Long> groupByStatusAndCount(String username) {
		Criteria[] arr = new Criteria[1];
		arr[0] = Criteria.where("isDelete").is(false).and("username").is(username);

		GroupByResults<HashMap> result = mongoTemplate.group(new Criteria().andOperator(arr), "order",
				GroupBy.key("status").initialDocument("{ count: 0 }")
						.reduceFunction("function(curr, result) { result.count += 1 }"),
				HashMap.class);
		// Lấy ra tổng số bản ghi;
		Long total = (long) result.getCount();
		Map<String, Long> rs = ((List<Map>) result.getRawResults().get("retval")).stream()
				.collect(Collectors.toMap(s -> (String) s.get("status"), s -> ((Double) s.get("count")).longValue()));
		rs.put("TOTAL", total);
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.OrderCustomRepository#filter(java.lang.String,
	 * java.lang.Boolean, java.lang.String, java.lang.String)
	 */
	@Override
	public SodSearchResult<Order> filter(String param, String keyword, PageRequest page, CurrentUser currentUser) {
		List<Criteria> criteriaList = new ArrayList<>();
		Query query = new Query();
		if (currentUser.isCustomer()) {
			criteriaList.add(Criteria.where("isDelete").is(false).and("username").is(currentUser.getUserName()));
		} else {
			criteriaList.add(Criteria.where("isDelete").is(false));
		}

		if (!Strings.isNullOrEmpty(keyword)) {
			if (Strings.isNullOrEmpty(param) || param.equals("all")) {
				query = query.addCriteria(new Criteria()
						.orOperator(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("staff.code").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"))
						.andOperator(criteriaList.toArray(new Criteria[0])));

			} else {
				if (param.equals("url")) {
					criteriaList.add(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("status")) {
					criteriaList.add(Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("emplyeeCode")) {
					criteriaList.add(Criteria.where("staff.code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("username")) {
					criteriaList.add(Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("code")) {
					criteriaList.add(Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
			}
		} else {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		List<Order> rsTotal = mongoTemplate.find(query, Order.class);
		query.with(page);
		List<Order> rsPage = mongoTemplate.find(query, Order.class);
		SodSearchResult<Order> searchRS = new SodSearchResult<>();
		searchRS.setItems(rsPage);
		searchRS.setTotalRecord(rsTotal.size());
		searchRS.setTotalPages(Math.floorDiv(rsTotal.size(), page.getPageSize()) + 1);
		return searchRS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.OrderCustomRepository#filterForCMS(java.lang.
	 * String, java.lang.String, org.springframework.data.domain.PageRequest,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterForCMS(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser) {
		List<Criteria> criteriaList = new ArrayList<>();
		Query query = new Query();
		if (currentUser.isCustomer()) {
			return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
		} else {
			criteriaList
					.add(Criteria.where("isDelete").is(false).and("staff").is(null));
		}

		if (!Strings.isNullOrEmpty(keyword)) {
			if (Strings.isNullOrEmpty(param) || param.equals("all")) {
				query = query.addCriteria(new Criteria()
						.orOperator(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("staff.code").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"))
						.andOperator(criteriaList.toArray(new Criteria[0])));

			} else {
				if (param.equals("url")) {
					criteriaList.add(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("status")) {
					criteriaList.add(Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("emplyeeCode")) {
					criteriaList.add(Criteria.where("staff.code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("username")) {
					criteriaList.add(Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("code")) {
					criteriaList.add(Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
			}
		} else {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		List<Order> rsTotal = mongoTemplate.find(query, Order.class);
		query.with(pageable);
		List<Order> rsPage = mongoTemplate.find(query, Order.class);
		SodSearchResult<Order> searchRS = new SodSearchResult<>();
		searchRS.setItems(rsPage);
		searchRS.setTotalRecord(rsTotal.size());
		searchRS.setTotalPages(Math.floorDiv(rsTotal.size(), pageable.getPageSize()) + 1);
		return searchRS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#receive(java.util.
	 * Collection, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> receive(Collection<Order> entities, CurrentUser currentUser) {
		Query query = new Query();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code")
				.in(entities.stream().map(x -> x.getCode()).collect(Collectors.toList())).and("staff").is(null));
		Update update = new Update();
		StaffDTO staff = StaffDTO.builder().code(currentUser.getCode()).name(currentUser.getFullName())
				.email(currentUser.getEmail()).build();
		update.set("staff", staff);
		update.set("timeReceive", LocalDateTime.now());
		update.set("lastModifiedBy", currentUser.getCode());
		update.set("lastModifiedDate", LocalDateTime.now());
		try {
			var rs = mongoTemplate.updateMulti(query, update, Order.class);
			return SodResult.<Boolean>builder().result(rs.isModifiedCountAvailable()).build();
		} catch (Exception e) {
			return SodResult.<Boolean>builder().isError(true).message(e.getMessage()).build();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.OrderCustomRepository#filterForMyOrder(java.
	 * lang.String, java.lang.String, org.springframework.data.domain.PageRequest,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterForMyOrder(String param, String keyword, PageRequest pageable,
			CurrentUser currentUser) {
		List<Criteria> criteriaList = new ArrayList<>();
		Query query = new Query();
		if (currentUser.isCustomer()) {
			return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
		} else {
			criteriaList.add(Criteria.where("isDelete").is(false).and("staff.code").is(currentUser.getCode()));
		}

		if (!Strings.isNullOrEmpty(keyword)) {
			if (Strings.isNullOrEmpty(param) || param.equals("all")) {
				query = query.addCriteria(new Criteria()
						.orOperator(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"))
						.andOperator(criteriaList.toArray(new Criteria[0])));

			} else {
				if (param.equals("url")) {
					criteriaList.add(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("status")) {
					criteriaList.add(Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("code")) {
					criteriaList.add(Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
			}
		} else {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		List<Order> rsTotal = mongoTemplate.find(query, Order.class);
		query.with(pageable);
		List<Order> rsPage = mongoTemplate.find(query, Order.class);
		SodSearchResult<Order> searchRS = new SodSearchResult<>();
		searchRS.setItems(rsPage);
		searchRS.setTotalRecord(rsTotal.size());
		searchRS.setTotalPages(Math.floorDiv(rsTotal.size(), pageable.getPageSize()) + 1);
		return searchRS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#update(com.sodo.
	 * xmarketing.dto.OrderDTO, java.lang.String,
	 * com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Order> update(OrderDTO order, String code, CurrentUser currentUser) {

		Query query = new Query();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code").is(code));
		Order entity = mongoTemplate.findOne(query, Order.class);

		if (entity.getStatus().ordinal() > order.getStatus().ordinal()) {
			return SodResult.<Order>builder().isError(true)
					.message("Can not update when current status order less than !").build();
		}

		Update update = new Update();
		if (entity.getStatus().ordinal() < order.getStatus().ordinal()
				&& order.getStatus().equals(OrderStatus.RUNNING)) {
			update.set("dateStart", LocalDateTime.now());
		} else if (entity.getStatus().ordinal() < order.getStatus().ordinal()
				&& (order.getStatus().equals(OrderStatus.CANCEL) || order.getStatus().equals(OrderStatus.COMPLETED))) {
			update.set("dateFinish", LocalDateTime.now());
		}
		update.set("start", order.getStart());
		update.set("current", order.getCurrent());
		update.set("note", order.getNote());
		update.set("status", order.getStatus());
		update.set("lastModifiedBy", currentUser.getCode());
		update.set("lastModifiedDate", LocalDateTime.now());

		try {
			Order rs = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true),
					Order.class);
			return SodResult.<Order>builder().result(rs).build();
		} catch (Exception e) {
			return SodResult.<Order>builder().isError(true).message(e.getMessage()).build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#refund(com.sodo.
	 * xmarketing.dto.OrderDTO, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Boolean> refund(String code, OrderDTO entity, CurrentUser currentUser) {

		Query query = new Query();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code").is(code));

		Order order = mongoTemplate.findOne(query, Order.class);

		// Kiểm tra trạng thái đơn hàng, nếu đơn hàng đang ở trạng thái CANCEL hoặc
		// COMPLETED thì mới cho hoàn tiền;
		if (order.getStatus().equals(OrderStatus.NEW) || order.getStatus().equals(OrderStatus.RUNNING)) {
			return SodResult.<Boolean>builder().isError(true).build();
		}

		// đơn hàng nào đã hoàn tiền rồi thì không cho hoàn tiền nữa
		if (null != order.getRefund() || !Strings.isNullOrEmpty(order.getReason())) {
			return SodResult.<Boolean>builder().isError(true).build();
		}

		// nếu số tiền hoàn trả lớn hơn số tiền hàng thì cũng không cho hoàn tiền
		if (order.getPrice().compareTo(entity.getRefund()) < 0) {
			return SodResult.<Boolean>builder().isError(true).build();
		}

		Update update = new Update();
		update.set("refund", entity.getRefund());
		update.set("reason", entity.getReason());
		try {
			var rs = mongoTemplate.updateFirst(query, update, Order.class);
			return SodResult.<Boolean>builder().result(rs.isModifiedCountAvailable()).build();
		} catch (Exception e) {
			return SodResult.<Boolean>builder().isError(true).result(false).build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.OrderCustomRepository#updateStatus(java.util.
	 * Collection)
	 */
	@Override
	public Boolean updateStatus(Collection<Order> orders, OrderStatus status) {
		Query query = new Query();
		query.addCriteria(Criteria.where("isDelete").is(false).and("code")
				.in(orders.stream().map(x -> x.getCode()).collect(Collectors.toList())));
		Update update = new Update();
		update.set("status", status);
		var rs = mongoTemplate.updateMulti(query, update, Order.class);
		return rs.isModifiedCountAvailable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.OrderCustomRepository#cmsCountByStatus(java.
	 * lang.Boolean, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Map<String, Long> cmsCountByStatus(Boolean isToday, String role, CurrentUser currentUser) {
		if (!currentUser.getRoles().contains(role)) {
			return new HashMap<>();
		}
		List<Criteria> arr = new ArrayList<>();
		if (Role.ROLE_ADMIN.toString().equals(role)) {
			arr.add(Criteria.where("isDelete").is(false));
			if (isToday != null && isToday) {
				arr.add(new Criteria().orOperator(
						Criteria.where("dateStart")
								.gte(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 0, 0))
								.lte(LocalDateTime.of(
										LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 23, 59)),
						Criteria.where("dateFinish")
								.gte(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 0, 0))
								.lte(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 23, 59))));
			}
			GroupByResults<HashMap> result = mongoTemplate.group(
					new Criteria().andOperator(arr.toArray(new Criteria[arr.size()])), "order",
					GroupBy.key("status").initialDocument("{ count: 0 }")
							.reduceFunction("function(curr, result) { result.count += 1 }"),
					HashMap.class);

			// Lấy ra tổng số bản ghi;
			Long total = (long) result.getCount();
			Map<String, Long> rs = ((List<Map>) result.getRawResults().get("retval")).stream().collect(
					Collectors.toMap(s -> (String) s.get("status"), s -> ((Double) s.get("count")).longValue()));
			rs.put("TOTAL", total);
			return rs;
		} else if (Role.ROLE_STAFF.toString().equals(role)) {
			arr.add(Criteria.where("isDelete").is(false).and("staff.code").is(currentUser.getCode()));
			if (isToday != null && isToday) {
				arr.add(new Criteria().orOperator(
						Criteria.where("dateStart")
								.gte(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 0, 0))
								.lte(LocalDateTime.of(
										LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 23, 59)),
						Criteria.where("dateFinish")
								.gte(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 0, 0))
								.lte(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
										LocalDateTime.now().getDayOfMonth(), 23, 59))));
			}
			GroupByResults<HashMap> result = mongoTemplate.group(
					new Criteria().andOperator(arr.toArray(new Criteria[arr.size()])), "order",
					GroupBy.key("status").initialDocument("{ count: 0 }")
							.reduceFunction("function(curr, result) { result.count += 1 }"),
					HashMap.class);
			// Lấy ra tổng số bản ghi;
			Long total = (long) result.getCount();
			Map<String, Long> rs = ((List<Map>) result.getRawResults().get("retval")).stream().collect(
					Collectors.toMap(s -> (String) s.get("status"), s -> ((Double) s.get("count")).longValue()));
			rs.put("TOTAL", total);
			return rs;
		}
		return new HashMap<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.repository.OrderCustomRepository#cmsTurnover(java.time.
	 * LocalDate, java.lang.String, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public Map<String, BigDecimal> cmsTurnover(LocalDate dateTime, String role, CurrentUser currentUser) {
		if (!currentUser.getRoles().contains(role)) {
			return new HashMap<>();
		}
		List<Criteria> arr = new ArrayList<>();
		if (Role.ROLE_ADMIN.toString().equals(role)) {
			arr.add(Criteria.where("isDelete").is(false));
			arr.add(Criteria.where("status").is(OrderStatus.COMPLETED));
			if(dateTime !=null)
				arr.add(Criteria.where("dateFinish")
					.gte(LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 0, 0))
					.lte(LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 23, 59)));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria().andOperator(arr.toArray(new Criteria[arr.size()]))),
					Aggregation.group("format.currencyName").sum("price").as("total"));

			AggregationResults<HashMap> groupResults = mongoTemplate.aggregate(aggregation, Order.class, HashMap.class);

			Map<String, BigDecimal> rs = new HashMap<>();
			rs = groupResults.getMappedResults().stream()
					.collect(Collectors.toMap(x -> ((HashMap) x).get("_id").toString(),
							x -> new BigDecimal(((HashMap) x).get("total").toString())));
			return rs;
		} else if (Role.ROLE_STAFF.toString().equals(role)) {
			arr.add(Criteria.where("isDelete").is(false).and("staff.code").is(currentUser.getCode()));
			arr.add(Criteria.where("status").is(OrderStatus.COMPLETED));
			if(dateTime !=null)
				arr.add(Criteria.where("dateFinish")
					.gte(LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 0, 0))
					.lte(LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 23, 59)));
			
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria().andOperator(arr.toArray(new Criteria[arr.size()]))),
					Aggregation.group("format.currencyName").sum("price").as("total"));

			AggregationResults<HashMap> groupResults = mongoTemplate.aggregate(aggregation, Order.class, HashMap.class);

			Map<String, BigDecimal> rs = new HashMap<>();
			rs = groupResults.getMappedResults().stream()
					.collect(Collectors.toMap(x -> ((HashMap) x).get("_id").toString(),
							x -> new BigDecimal(((HashMap) x).get("total").toString())));
			return rs;
		}
		return new HashMap<>();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#filterV2(java.lang.String, java.lang.String, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser, java.lang.String)
	 */
	@Override
	public SodSearchResult<Order> filterV2(String param, String keyword, PageRequest pageable, CurrentUser currentUser,
			String role) {
		List<Criteria> criteriaList = new ArrayList<>();
		Query query = new Query();
		if (currentUser.isCustomer()) {
			criteriaList.add(Criteria.where("isDelete").is(false).and("username").is(currentUser.getUserName()));
		} else {
			if (!currentUser.getRoles().contains(role)) {
				return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
			}
			if(Role.ROLE_ADMIN.toString().equals(role))
				criteriaList.add(Criteria.where("isDelete").is(false));
			else if(Role.ROLE_STAFF.toString().equals(role)) {
				criteriaList.add(Criteria.where("isDelete").is(false).and("staff.code").is(currentUser.getCode()));
			}
			else if(Role.ROLE_SALE.toString().equals(role)) {
				criteriaList.add(Criteria.where("isDelete").is(false).and("sale.code").is(currentUser.getCode()));
			} else 
				return SodSearchResult.<Order>builder().items(null).totalPages(0).totalRecord(0).build();
		}

		if (!Strings.isNullOrEmpty(keyword)) {// trường hợp có keyword
			if (Strings.isNullOrEmpty(param) || param.equals("all")) { // có keyword nhưng không rõ param thì tìm kiếm theo tất cả các param
				query = query.addCriteria(new Criteria()
						.orOperator(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("staff.code").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"),
								Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"))
						.andOperator(criteriaList.toArray(new Criteria[0])));

			} else { // tìm kiếm theo param đã xác định
				if (param.equals("url")) {
					criteriaList.add(Criteria.where("url").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("status")) {
					criteriaList.add(Criteria.where("status").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("emplyeeCode")) {
					criteriaList.add(Criteria.where("staff.code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("username")) {
					criteriaList.add(Criteria.where("username").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("code")) {
					criteriaList.add(Criteria.where("code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				if (param.equals("saleCode")) {
					criteriaList.add(Criteria.where("sale.code").regex(Pattern.compile(keyword).pattern(), "i"));
				}
				query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
			}
		} else { // nếu không có keyword tìm kiếm nào thì lấy tất cả
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		List<Order> rsTotal = mongoTemplate.find(query, Order.class);
		query.with(pageable);
		List<Order> rsPage = mongoTemplate.find(query, Order.class);
		SodSearchResult<Order> searchRS = new SodSearchResult<>();
		searchRS.setItems(rsPage);
		searchRS.setTotalRecord(rsTotal.size());
		searchRS.setTotalPages(Math.floorDiv(rsTotal.size(), pageable.getPageSize()) + 1);
		return searchRS;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#filterV3(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterV3(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser) {
		Query query = new Query();
		SodSearchResult<Order> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(orderSearch);

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, Order.class));
		query.with(pageable);
		result.setItems(mongoTemplate.find(query, Order.class));
		return result;
	}
	
	@Override
	public SodSearchResult<OrderExcel> filterV3ForExcel(OrderSearch orderSearch, PageRequest pageable, CurrentUser currentUser) {
		Query query = new Query();
		SodSearchResult<OrderExcel> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(orderSearch);


		
		LookupOperation lookupOperation = LookupOperation.newLookup().from("order-distributor")
		        .localField("code").foreignField("orderCode").as("orderDistributors");
	    Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(new Criteria().andOperator(criterias.toArray( new Criteria[criterias.size()]))),
	            lookupOperation);
		    AggregationResults<OrderWithNCC> rs =
		        mongoTemplate.aggregate(aggregation, Order.class, OrderWithNCC.class);
		    List<OrderWithNCC> listOrderWithNCC =  rs.getMappedResults();
		    List<OrderExcel> orderExcels = new ArrayList<>();
		    for(OrderWithNCC order: listOrderWithNCC) {
		    	OrderExcel orderexcel = (OrderExcel) order;
		    	if(!order.getOrderDistributors().isEmpty()) {
		    		for(OrderDistributor distributor: order.getOrderDistributors()) {
		    			orderexcel.setOrderDistributor(distributor);
		    			orderexcel.convertBeforExport();
		    			orderExcels.add(orderexcel);
		    		}
		    	} else {
		    		orderExcels.add(orderexcel);
		    	}
		    }

		result.setTotalRecord(mongoTemplate.count(query, Order.class));
		query.with(pageable);
		result.setItems(orderExcels);
		return result;
	}
	
	
	private List<Criteria> createCriteriaFilter(OrderSearch data) {
		Criteria criteriaAmount;
		Criteria criteriaCreatedDate = Criteria.where("createdDate");
		List<Criteria> criterias = new ArrayList<>();

		if (!Strings.isNullOrEmpty(data.getOrderCode())) {
			criterias.add(Criteria.where("code").is(data.getOrderCode()));
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
		if (!Strings.isNullOrEmpty(data.getUsername())) {
			criterias.add(Criteria.where("username").is(data.getUsername()));
		}

		if (!Strings.isNullOrEmpty(data.getStaffCode())) {
			criterias.add(Criteria.where("staff.code").is(data.getStaffCode()));
		}
		
		if (!Strings.isNullOrEmpty(data.getSaleCode())) {
			criterias.add(Criteria.where("sale.code").is(data.getSaleCode()));
		}
		
		if (!Strings.isNullOrEmpty(data.getServiceCode())) {
			criterias.add(Criteria.where("service.code").is(data.getServiceCode()));
		}
		
		if (!Strings.isNullOrEmpty(data.getUrl())) {
			criterias.add(Criteria.where("url").is(data.getUrl()));
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

		Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
		criterias.add(criteriaIsDelete);

		return criterias;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#filterReceiveV3(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterReceiveV3(OrderSearch orderSearch, PageRequest pageable,
			CurrentUser currentUser) {
		Query query = new Query();
		SodSearchResult<Order> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(orderSearch);
		criterias.add(Criteria.where("staff").is(null));

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, Order.class));
		query.with(pageable);
		result.setItems(mongoTemplate.find(query, Order.class));
		return result;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#filterForMyOrderV3(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterForMyOrderV3(OrderSearch orderSearch, PageRequest pageable,
			CurrentUser currentUser) {
		
		Query query = new Query();
		SodSearchResult<Order> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(orderSearch);
		criterias.add(Criteria.where("staff.code").is(currentUser.getCode()));

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, Order.class));
		query.with(pageable);
		result.setItems(mongoTemplate.find(query, Order.class));
		return result;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.OrderCustomRepository#filterOrderForSales(com.sodo.xmarketing.dto.OrderSearch, org.springframework.data.domain.PageRequest, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodSearchResult<Order> filterOrderForSales(OrderSearch orderSearch, PageRequest pageable,
			CurrentUser currentUser) {
		
		Query query = new Query();
		SodSearchResult<Order> result = new SodSearchResult<>();
		List<Criteria> criterias = createCriteriaFilter(orderSearch);
		criterias.add(Criteria.where("sale.code").is(currentUser.getCode()));

		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}

		result.setTotalRecord(mongoTemplate.count(query, Order.class));
		query.with(pageable);
		result.setItems(mongoTemplate.find(query, Order.class));
		return result;
	}
}
