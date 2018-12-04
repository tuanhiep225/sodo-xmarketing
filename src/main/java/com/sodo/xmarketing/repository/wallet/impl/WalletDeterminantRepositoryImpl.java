/**
 * 
 */
package com.sodo.xmarketing.repository.wallet.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

import com.google.common.base.Strings;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.wallet.WalletDeterminant;
import com.sodo.xmarketing.repository.wallet.WalletDeterminantCustomRepository;
import com.sodo.xmarketing.status.SystemE;

/**
 * @author tuanhiep225
 *
 */

public class WalletDeterminantRepositoryImpl implements WalletDeterminantCustomRepository {

	private static final Log LOGGER = LogFactory.getLog(WalletDeterminantRepositoryImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.wallet.WalletDeterminantCustomRepository#
	 * getWalletByFilter(java.lang.Boolean, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Boolean,
	 * org.springframework.data.domain.Pageable)
	 */
	@Override
	public Map<String, Object> getWalletByFilter(Boolean absoluteParent, String name, String parent, String type,
			Boolean status, Pageable pageable) throws SodException {
		Map<String, Object> result = new HashMap<>();

		// ----------------------
		List<WalletDeterminant> wallets = null;
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
		Criteria criteriaSystem = Criteria.where("system").is(true);
		criterias.add(criteriaSystem);
		Criteria excludeCrite = Criteria.where("code").nin(SystemE.DK_DEPOSIT, SystemE.DK_WITHDRAWAL, SystemE.DK_SUPER);
		criterias.add(excludeCrite);
		criterias.add(Criteria.where("system").is(true));
		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));
		}
		Long countRecord = mongoTemplate.count(query, WalletDeterminant.class);
		result.put("count", countRecord);
		query.with(pageable);
		try {
			wallets = mongoTemplate.find(query, WalletDeterminant.class);
		} catch (Exception e) {
			throw new SodException(e.getLocalizedMessage(), e.getMessage());
		}
		result.put("wallets", wallets);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.wallet.WalletDeterminantCustomRepository#
	 * suggestWalletDeter(java.lang.String, int)
	 */
	@Override
	public List<WalletDeterminant> suggestWalletDeter(String query, int numberRecord) throws SodException {
		List<WalletDeterminant> wallets = null;
		Query queryCri = new Query();
		List<Criteria> criterias = new ArrayList<>();
		Criteria isDeleteCriteria = Criteria.where("isDelete").is(false);
		Criteria textSearchCriteria = Criteria.where("textSearch")
				.regex(Pattern.compile(query, Pattern.CASE_INSENSITIVE));
		Criteria filterCriteria = Criteria.where("code").nin();
		criterias.add(isDeleteCriteria);
		criterias.add(textSearchCriteria);
		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			queryCri.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray))).limit(numberRecord);
		}
		try {
			wallets = mongoTemplate.find(queryCri, WalletDeterminant.class);

		} catch (Exception e) {
			throw new SodException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());

		}
		return wallets;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.wallet.WalletDeterminantCustomRepository#
	 * getDeterminalTree(int, boolean)
	 */
	@Override
	public List<WalletDeterminant> getDeterminalTree(int maxLevel, boolean containSystem) throws SodException {
		List<WalletDeterminant> wallets = null;
		Query queryCri = new Query();
		List<Criteria> criterias = new ArrayList<>();
		Criteria isDeleteCriteria = Criteria.where("isDelete").is(false);
		Criteria enabledCriteria = Criteria.where("status").is(true);
		Criteria systemCriteria = Criteria.where("system").is(true);
		Criteria levelCriteria = Criteria.where("level").lte(maxLevel);

		criterias.add(isDeleteCriteria);
		criterias.add(enabledCriteria);
		criterias.add(systemCriteria);

		criterias.add(levelCriteria);
		if (!criterias.isEmpty()) {
			Criteria[] andCriteriaAray = new Criteria[criterias.size()];
			queryCri.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray)));

		}
		try {
			wallets = mongoTemplate.find(queryCri, WalletDeterminant.class);

		} catch (Exception e) {
			throw new SodException(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());

		}
		return wallets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.wallet.WalletDeterminantCustomRepository#
	 * checkDuplicate(java.lang.String, java.lang.String)
	 */
	@Override
	public WalletDeterminant checkDuplicate(String fieldName, String value) throws SodException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.repository.wallet.WalletDeterminantCustomRepository#
	 * getWalletDeterminantsFromCodes(java.util.List)
	 */
	@Override
	public Map<String, WalletDeterminant> getWalletDeterminantsFromCodes(List<String> code) {
	    Map<String, WalletDeterminant> result = new HashMap<>();
	    // Criteria criteriaIsDelete = Criteria.where("isDelete").is(false);
	    Criteria criteriaCodeList = Criteria.where("code").in(code);
	    List<Criteria> criterias = new ArrayList<>();

	    // criterias.add(criteriaIsDelete);
	    criterias.add(criteriaCodeList);
	    Query query = new Query();

	    if (!criterias.isEmpty()) {
	      Criteria[] andCriteriaAray = new Criteria[criterias.size()];
	      query.addCriteria(new Criteria().andOperator(criterias.toArray(andCriteriaAray))).fields()
	          .include("name").include("code").include("id");
	    }
	    List<WalletDeterminant> data = mongoTemplate.find(query, WalletDeterminant.class);
	    for (WalletDeterminant walletDeterminant : data) {
	      result.put(walletDeterminant.getCode(), walletDeterminant);
	    }
	    return result;
	}
}
