package com.sodo.xmarketing.repository.fund.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.FundSearchDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.fund.Fund;
import com.sodo.xmarketing.repository.fund.FundCustomRepository;
import com.sodo.xmarketing.service.impl.NextSequenceService;
import com.sodo.xmarketing.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;

public class FundRepositoryImpl implements FundCustomRepository {

    @Autowired
    NextSequenceService sequenceService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Fund checkDuplicate(String fieldName, String value) throws SodException {

        Fund fund = new Fund();
        Query queryCri = new Query();
        List<Criteria> criterias = new ArrayList<>();
        Criteria isDeleteCriteria = Criteria.where("isDelete").is(false);
        Criteria textSearchCriteria = Criteria.where(fieldName).is(value);
        criterias.add(isDeleteCriteria);
        criterias.add(textSearchCriteria);
        if (!criterias.isEmpty()) {
            Criteria[] andCriteriaAray = new Criteria[criterias.size()];
            queryCri.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
        }
        try {
            List<Fund> listFund = mongoTemplate.find(queryCri, Fund.class);
            if (listFund != null && !listFund.isEmpty()) {
                fund = listFund.get(0);
            }

        } catch (Exception e) {
            throw new SodException(e.getLocalizedMessage(), e.getMessage());

        }
        return fund;
    }

    @Override
    public Map<String, Object> filterFund(String code, String type, String currency, String name,
            String managerCode, String country, String enabled, Pageable pageable,
            String currentUserCode) throws SodException {


        Map<String, Object> result = new HashMap<>();
        List<Fund> listFund = new ArrayList<>();
        Query query = new Query();
        Criteria typeCriteria;
        Criteria codeCriteria;
        Criteria currencyCriteria;
        Criteria nameCriteria;
        Criteria managerCodeCriteria;
        Criteria countryCriteria;
        Criteria enabledCriteria;
        Criteria currentUserCodeCriteria;
        List<Criteria> criterias = new ArrayList<>();
        Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
        criterias.add(criteriaIsDelete);
        if (!Strings.isNullOrEmpty(code)) {
            codeCriteria = Criteria.where("code").is(code);
            criterias.add(codeCriteria);
        }
        if (!Strings.isNullOrEmpty(type)) {
            typeCriteria = Criteria.where("type").is(type);
            criterias.add(typeCriteria);
        }

        if (!Strings.isNullOrEmpty(currency)) {
            currencyCriteria = Criteria.where("currency").is(currency);
            criterias.add(currencyCriteria);
        }

        if (!Strings.isNullOrEmpty(name)) {
            String nameSearch = StringUtils.unAccent(name).toLowerCase();
            nameCriteria = Criteria.where("textSearch").regex(nameSearch);
            criterias.add(nameCriteria);
        }

        if (!Strings.isNullOrEmpty(managerCode)) {
            managerCodeCriteria = Criteria.where("managerCode").is(managerCode);
            criterias.add(managerCodeCriteria);
        }

        if (!Strings.isNullOrEmpty(country)) {
            countryCriteria = Criteria.where("country").is(country);
            criterias.add(countryCriteria);
        }

        if (!Strings.isNullOrEmpty(enabled)) {
            enabledCriteria = Criteria.where("enabled").is(Boolean.parseBoolean(enabled));
            criterias.add(enabledCriteria);
        }

        if (!Strings.isNullOrEmpty(currentUserCode)) {
            currentUserCodeCriteria = Criteria.where("allowedEmployees").in(currentUserCode);
            criterias.add(currentUserCodeCriteria);
        }

        if (!criterias.isEmpty()) {
            Criteria[] andCriteriaAray = new Criteria[criterias.size()];
            query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
        }
        Long count = mongoTemplate.count(query, Fund.class);
        result.put("count", count);
        query.with(pageable);
        try {
            listFund = mongoTemplate.find(query, Fund.class);
            result.put("data", listFund);
        } catch (Exception e) {
            throw new SodException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());

        }
        return result;
    }

    @Override
    public List<Fund> suggestFund(String query, int numberRecord, String employeeCode)
            throws SodException {

        List<Fund> listFund = new ArrayList<>();
        Query queryCri = new Query();
        List<Criteria> criterias = new ArrayList<>();
        Criteria isDeleteCriteria = Criteria.where("isDelete").is(false);
        Criteria textSearchCriteria = Criteria.where("textSearch")
                .regex(Pattern.compile(query, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
        if (!Strings.isNullOrEmpty(employeeCode)) {
            Criteria allowAccess = Criteria.where("allowedEmployees").elemMatch(Criteria.where("code").is(employeeCode));
            criterias.add(allowAccess);
        }
        criterias.add(isDeleteCriteria);
        criterias.add(Criteria.where("enabled").is(true));
        if (!Strings.isNullOrEmpty(query)) {
            criterias.add(textSearchCriteria);
        }

        if (!criterias.isEmpty()) {
            Criteria[] andCriteriaAray = new Criteria[criterias.size()];
            queryCri.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)))
                    .limit(numberRecord);
        }
        try {
            listFund = mongoTemplate.find(queryCri, Fund.class);
        } catch (Exception e) {
            throw new SodException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());

        }
        return listFund;
    }

    @Override
    public List<Fund> getByTypeAndCurrencyAndEmployeeCode(String[] type, String currency,
            String employeeCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").in(type).and("currency").is(currency)
                .and("enabled").is(true).and("isDelete").is(false));
        // todo: Thiếu lọc theo nhân viên

        return mongoTemplate.find(query, Fund.class);
    }

    @Override
    public Map<String, Fund> getFromCodes(List<String> code) {

        Map<String, Fund> result = new HashMap<>();
        // Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
        Criteria criteriaCodeList = Criteria.where("code").in(code);
        List<Criteria> criterias = new ArrayList<>();

        // criterias.add(criteriaIsDelete);
        criterias.add(criteriaCodeList);
        Query query = new Query();

        if (!criterias.isEmpty()) {
            Criteria[] andCriteriaAray = new Criteria[criterias.size()];
            query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
        }
        List<Fund> data = mongoTemplate.find(query, Fund.class);
        for (Fund fund : data) {
            result.put(fund.getCode(), fund);
        }
        return result;
    }

    @Override
    public Fund updateField(String code, CurrentUser currentUser, String action, String fieldName,
            Map<String, Object> value) {


        Query query = new Query();
        Query queryCode = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        queryCode.addCriteria(Criteria.where("code").is(code));
        Update update = new Update();
        update.set("lastModifiedDate",
                mongoTemplate.getConverter().convertToMongoType(LocalDateTime.now()));
        if (fieldName != "balance") {
            update.set(fieldName, value.get(fieldName));
        } else {
            update.inc("balance", new BigDecimal(value.get("balance").toString()));
        }
        mongoTemplate.updateFirst(query, update, Fund.class);

        return mongoTemplate.findOne(queryCode, Fund.class);
    }

    @Override
    public boolean updateFieldsFund(String code, Map<String, Object> mapFields,
            CurrentUser currentUser) {


        if (mapFields.isEmpty()) {
            return false;
        }

        Query query = new Query();
        MongoConverter mongoConverter = mongoTemplate.getConverter();

        query.addCriteria(Criteria.where("code").is(code));
        Update update = new Update();

        mapFields
                .forEach((key, value) -> update.set(key, mongoConverter.convertToMongoType(value)));

        if (currentUser != null && !currentUser.isCustomer()) {
            update.set("lastModifiedDate", mongoConverter.convertToMongoType(LocalDateTime.now()));
            update.set("lastModifiedBy", currentUser.getUserName());
        }
        mongoTemplate.updateFirst(query, update, Fund.class);

        return true;
    }

    @Override
    public SodSearchResult<Fund> getFundList(FundSearchDTO fundSearch, Pageable pageable) {

        SodSearchResult<Fund> result = new SodSearchResult<>();

        Query query = new Query();

        query.addCriteria(Criteria.where("isDelete").is(false));

        if (!Strings.isNullOrEmpty(fundSearch.getFundType())) {
            query.addCriteria(Criteria.where("type").is(fundSearch.getFundType()));
        }

        if (!Strings.isNullOrEmpty(fundSearch.getCurrency())) {
            query.addCriteria(Criteria.where("format.currency").is(fundSearch.getCurrency()));
        }

        if (!Strings.isNullOrEmpty(fundSearch.getName())) {
            query.addCriteria(Criteria.where("name")
                    .regex(Pattern.compile(fundSearch.getName(), Pattern.CASE_INSENSITIVE)));
        }

        if (!Strings.isNullOrEmpty(fundSearch.getManager())) {
            query.addCriteria(Criteria.where("manager.code").is(fundSearch.getManager()));
        }

        if (fundSearch.getStatus() != null) {
            query.addCriteria(Criteria.where("enabled").is(fundSearch.getStatus()));
        }


        if (!Strings.isNullOrEmpty(fundSearch.getFundGroupCode())) {
            query.addCriteria(
                    Criteria.where("categoryGroupFundCode").is(fundSearch.getFundGroupCode()));
        }

        result.setTotalRecord(mongoTemplate.count(query, Fund.class));

        query.with(pageable);

        result.setItems(mongoTemplate.find(query, Fund.class));

        return result;
    }

    @Override
    public List<Fund> getFundByType() {

        Query query = new Query();

        List<Criteria> criterias = new ArrayList<>();

        Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
        Criteria criteriaEnabled = Criteria.where("enabled").is(true);
        Criteria criteriaCurrency = Criteria.where("currency").ne("CNY");

        criterias.add(criteriaIsDelete);
        criterias.add(criteriaEnabled);
        criterias.add(criteriaCurrency);

        if (!criterias.isEmpty()) {
            Criteria[] andCriteriaAray = new Criteria[criterias.size()];
            query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
        }

        return mongoTemplate.find(query, Fund.class);
    }

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.repository.fund.FundCustomRepository#getByGroupCodeAndEmployeeCode(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Fund> getByGroupCodeAndEmployeeCode(String groupCode, String employeeCode) {
		Query query = new Query();

        List<Criteria> criterias = new ArrayList<>();

        Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
        Criteria criteriaEnabled = Criteria.where("enabled").is(true);
        Criteria criteriaCurrency = Criteria.where("categoryGroupFundCode").is(groupCode);

        criterias.add(criteriaIsDelete);
        criterias.add(criteriaEnabled);
        criterias.add(criteriaCurrency);
        criterias.add(Criteria.where("allowedEmployees").elemMatch(Criteria.where("code").is(employeeCode)));

        if (!criterias.isEmpty()) {
            Criteria[] andCriteriaAray = new Criteria[criterias.size()];
            query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
        }
        return mongoTemplate.find(query, Fund.class);
	}

}
