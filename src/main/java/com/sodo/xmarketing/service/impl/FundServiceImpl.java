/**
 * 
 */
package com.sodo.xmarketing.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;
import com.sodo.xmarketing.dto.FundSearchDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Categories;
import com.sodo.xmarketing.model.common.SearchResultMap;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.fund.Fund;
import com.sodo.xmarketing.repository.fund.FundRepository;
import com.sodo.xmarketing.service.CategoriesService;
import com.sodo.xmarketing.service.FundService;
import com.sodo.xmarketing.utils.StringUtils;

/**
 * @author tuanhiep225
 *
 */
@Service
public class FundServiceImpl implements FundService {

	@Autowired
	private FundRepository fundRepository;

	@Autowired
	NextSequenceService sequenceService;

	@Autowired
	private CategoriesService categoriesService;

	private static final Log LOGGER = LogFactory.getLog(FundServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sodo.xmarketing.service.FundService#create(com.sodo.xmarketing.model.fund .Fund)
	 */
	@Override
	public Fund create(@Valid Fund fund) {

		fund.setCode(sequenceService.genFundCode());

		// Set lại balance về 0 khi tạo quỹ mới
		fund.setBalance(BigDecimal.ZERO);

		fund.setTextSearch(
				fund.getTextSearch() + " " + StringUtils.unAccent(fund.getCode()).toLowerCase());

		return fundRepository.add(fund);
	}

	@Override
	public Map<String, Object> filterFund(String code,String type, String currency, String name,
			String managerCode, String country, String enabled, PageRequest request,
			String currentUserCode) throws SodException {
		return fundRepository.filterFund(code, type, currency, name, managerCode, country, enabled,
				request, currentUserCode);
	}

	@Override
	public Fund checkDuplicate(String fieldName, String value) throws SodException {
		return fundRepository.checkDuplicate(fieldName, value);
	}

	@Override
	public SearchResultMap<Fund> getMapByFundGroupCode(FundSearchDTO fundSearch, int page,
			int size) {

		// Pageable and Sort
		PageRequest request =
				PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "lastModifiedDate"));

		SodSearchResult<Fund> fundList = fundRepository.getFundList(fundSearch, request);

		SearchResultMap<Fund> result = new SearchResultMap<>();

		result.setTotalRecord(fundList.getTotalRecord());
		result.setMapResult(fundList.getItems().stream()
				.collect(Collectors.groupingBy(fund -> fund.getCategoryGroupFundCode() == null ? ""
						: fund.getCategoryGroupFundCode())));
		return result;
	}

	@Override
	public List<Fund> suggestFund(String query, int numberRecord, String employeeCode)
			throws SodException {
		if (employeeCode == null) {
			employeeCode = "";
		}
		return fundRepository.suggestFund(query, numberRecord, employeeCode);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.FundService#getByGroupCodeAndEmployeeCode(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Fund> getByGroupCodeAndEmployeeCode(String groupCode, String employeeCode) {
		return fundRepository.getByGroupCodeAndEmployeeCode(groupCode,employeeCode);
	}
}
