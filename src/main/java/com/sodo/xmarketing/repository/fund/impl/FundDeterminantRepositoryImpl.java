/**
 *
 */
package com.sodo.xmarketing.repository.fund.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

import com.google.common.base.Strings;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.repository.fund.FundDeterminantCustomRepository;
import com.sodo.xmarketing.status.SystemE;

/**
 * @author ANH MINH - PC
 */
public class FundDeterminantRepositoryImpl implements FundDeterminantCustomRepository {

	@Autowired
  private MongoTemplate mongoTemplate;


  @Override
  public Map<String, Object> getFundByFilter(Boolean absoluteParent, String name, String parent,
      String type, Boolean status, Pageable pageable) throws SodException {
    Map<String, Object> result = new HashMap<>();

    // ----------------------
    List<FundDeterminant> funds = null;
    Query query = new Query();
    Criteria criteriaName;
    Criteria criteriaParent;
    Criteria criteriaType;
    Criteria criteriaStatus;

    List<Criteria> criterias = new ArrayList<>();

    if (!Strings.isNullOrEmpty(name)) {
      criteriaName = Criteria.where("name").is(name);
      criterias.add(criteriaName);
    }
    if (!Strings.isNullOrEmpty(parent)) {
      if (absoluteParent) {
        criteriaParent = Criteria.where("parent").is(parent);
        criterias.add(criteriaParent);
      } else {
        parent = parent + ".";
        String querrrry = parent.replace(".", "\\.");
        Pattern r = Pattern.compile("^" + querrrry);
        Criteria startWithTreeCode = Criteria.where("treeCode").regex(r);
        Criteria isTreeCode = Criteria.where("treeCode").is(parent);
        Criteria filterAndOwner = new Criteria().orOperator(startWithTreeCode);
        criterias.add(filterAndOwner);
      }

    }
    if (!Strings.isNullOrEmpty(type)) {
      criteriaType = Criteria.where("type").is(type);
      criterias.add(criteriaType);
    }
    if (status != null) {
      criteriaStatus = Criteria.where("status").is(status);
      criterias.add(criteriaStatus);
    }

    Criteria criteriaIsdelete = Criteria.where("isDelete").is(false);
    criterias.add(criteriaIsdelete);
    criterias.add(Criteria.where("system").is(true));
    Criteria excludeCrite =
        Criteria.where("code").nin(SystemE.DK_DEPOSIT, SystemE.DK_WITHDRAWAL, SystemE.DK_SUPER);
    criterias.add(excludeCrite);
    if (!criterias.isEmpty()) {
      Criteria[] andCriteriaAray = new Criteria[criterias.size()];
      query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
    }
    Long countRecord = mongoTemplate.count(query, FundDeterminant.class);
    result.put("count", countRecord);
    query.with(pageable);
    try {
      funds = mongoTemplate.find(query, FundDeterminant.class);
    } catch (Exception e) {
      throw new SodException(e.getLocalizedMessage(), e.getMessage());
    }
    result.put("funds", funds);

    return result;
  }

  @Override
  public List<FundDeterminant> suggestFundDeter(String query, int numberRecord)
      throws SodException {
    List<FundDeterminant> funds = null;
    Query queryCri = new Query();
    List<Criteria> criterias = new ArrayList<>();
    Criteria isDeleteCriteria = Criteria.where("isDelete").is(false);
    Criteria textSearchCriteria = Criteria.where("textSearch").regex(query);
    criterias.add(isDeleteCriteria);
    criterias.add(textSearchCriteria);
    if (!criterias.isEmpty()) {
      Criteria[] andCriteriaAray = new Criteria[criterias.size()];
      queryCri.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)))
          .limit(numberRecord);
    }
    try {
      funds = mongoTemplate.find(queryCri, FundDeterminant.class);


    } catch (Exception e) {
      throw new SodException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());

    }
    return funds;
  }

  @Override
  public List<FundDeterminant> getDeterminalTree(int maxLevel, boolean containSystem)
      throws SodException {
    List<FundDeterminant> funds = null;
    Query queryCri = new Query();
    List<Criteria> criterias = new ArrayList<>();
    Criteria isDeleteCriteria = Criteria.where("isDelete").is(false);
    Criteria enabledCriteria = Criteria.where("status").is(true);
    Criteria systemCriteria = Criteria.where("system").is(true);
    Criteria levelCriteria = Criteria.where("level").lte(maxLevel);

    criterias.add(isDeleteCriteria);
    criterias.add(enabledCriteria);
    criterias.add(systemCriteria);

    // criterias.add(levelCriteria);
    if (!criterias.isEmpty()) {
      Criteria[] andCriteriaAray = new Criteria[criterias.size()];
      queryCri.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));

    }
    try {
      funds = mongoTemplate.find(queryCri, FundDeterminant.class);


    } catch (Exception e) {
      throw new SodException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());

    }
    return funds;
  }

  @Override
  public FundDeterminant checkDuplicate(String fieldName, String value) throws SodException {
    FundDeterminant fund = new FundDeterminant();
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
      List<FundDeterminant> listFund = mongoTemplate.find(queryCri, FundDeterminant.class);
      if (listFund != null && !listFund.isEmpty()) {
        fund = listFund.get(0);
      }

    } catch (Exception e) {
      throw new SodException(e.getLocalizedMessage(), e.getMessage());

    }
    return fund;
  }

  @Override
  public Map<String, FundDeterminant> getFundDeterminantsFromCodes(List<String> code) {
    Map<String, FundDeterminant> result = new HashMap<>();
    Criteria criteriaCodeList = Criteria.where("code").in(code);
    List<Criteria> criterias = new ArrayList<>();

    criterias.add(criteriaCodeList);
    Query query = new Query();

    if (!criterias.isEmpty()) {
      Criteria[] andCriteriaAray = new Criteria[criterias.size()];
      query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray))).fields()
          .include("name").include("code").include("id");
    }
    List<FundDeterminant> data = mongoTemplate.find(query, FundDeterminant.class);
    for (FundDeterminant fundDeterminant : data) {
      result.put(fundDeterminant.getCode(), fundDeterminant);
    }
    return result;
  }

  @Override
  public List<FundDeterminant> getListTreeDeter(List<String> treeCodes) {

    Query query = new Query();

    query.addCriteria(
        Criteria.where("isDelete").is(false));
    query.addCriteria(
        Criteria.where("status").is(true));
    query.addCriteria(
        Criteria.where("system").is(true));

    if (treeCodes != null && !treeCodes.isEmpty()) {
      Criteria orTreeCode = new Criteria();

      List<Criteria> treeCodesCriteria = new ArrayList<>();
      for (String treeCode : treeCodes) {
        treeCodesCriteria.add(Criteria
            .where("treeCode").regex("^" + treeCode));
      }
      Criteria[] criteriaArray = new Criteria[treeCodes.size()];
      orTreeCode.orOperator(treeCodesCriteria.toArray(criteriaArray));

      query.addCriteria(orTreeCode);
    }

    return mongoTemplate.find(query, FundDeterminant.class);
  }
}
