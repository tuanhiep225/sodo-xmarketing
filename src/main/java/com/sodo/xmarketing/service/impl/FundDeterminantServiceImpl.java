/**
 * 
 */
package com.sodo.xmarketing.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.repository.fund.FundDeterminantRepository;
import com.sodo.xmarketing.repository.fund.FundTransactRepository;
import com.sodo.xmarketing.service.FundDeterminantService;
import com.sodo.xmarketing.service.InitDataHelper;
import com.sodo.xmarketing.utils.ConfigHelper;
import com.sodo.xmarketing.utils.Properties;

/**
 * @author tuanhiep225
 *
 */
@Service
public class FundDeterminantServiceImpl implements FundDeterminantService {

	@Autowired
	FundDeterminantRepository fundRepository;
	@Autowired
	NextSequenceService sequenceService;
//	@Autowired
//	FundTransactRepository fundTransactRepository;

	private InitDataHelper initDataHelper;

	@Autowired
	private ConfigHelper configHelper;
	
	@Autowired
	public FundDeterminantServiceImpl(Properties properties, MongoTemplate mongoTemplate, ConfigHelper configHelper) {
		initDataHelper = new InitDataHelper(properties, mongoTemplate, configHelper);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.FundDeterminantService#getFundByFilter(java.lang.
	 * Boolean, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Boolean, org.springframework.data.domain.PageRequest)
	 */
	@Override
	public Map<String, Object> getFundByFilter(Boolean absoluteParent, String name, String parent, String type,
			Boolean status, PageRequest pageable) throws SodException {
		if (!absoluteParent && !Strings.isNullOrEmpty(parent)) {
			FundDeterminant fundDeterminant = fundRepository.findByCode(parent);
			return fundRepository.getFundByFilter(absoluteParent, name, fundDeterminant.getTreeCode(), type, status,
					pageable);
		} else {
			return fundRepository.getFundByFilter(absoluteParent, name, parent, type, status, pageable);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundDeterminantService#findByCode(java.lang.
	 * String)
	 */
	@Override
	public FundDeterminant findByCode(String code) {
		return fundRepository.findByCode(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.FundDeterminantService#suggestFundDeter(java.lang
	 * .String, int)
	 */
	@Override
	public List<FundDeterminant> suggestFundDeter(String lowerCase, int numberRecord) throws SodException {
		return fundRepository.suggestFundDeter(lowerCase, numberRecord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.FundDeterminantService#getDeterminalTree(int,
	 * boolean)
	 */
	@Override
	public List<FundDeterminant> getDeterminalTree(int maxLevel, boolean containSystem) throws SodException {
		return fundRepository.getDeterminalTree(maxLevel, containSystem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundDeterminantService#
	 * getFundDeterminantsFromCodes(java.util.List)
	 */
	@Override
	public Map<String, FundDeterminant> getFundDeterminantsFromCodes(List<String> codes) {
		return fundRepository.getFundDeterminantsFromCodes(codes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sodo.xmarketing.service.FundDeterminantService#getListTreeDeter(java.util
	 * .List)
	 */
	@Override
	public List<FundDeterminant> getListTreeDeter(List<String> treeCodes) {
		return fundRepository.getListTreeDeter(treeCodes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundDeterminantService#initData(com.sodo.
	 * xmarketing.model.account.CurrentUser)
	 */
	@Override
	public void initData(CurrentUser currentUser) throws SodException {
		initDataHelper.initFundDeterminant(currentUser);

	}

}
