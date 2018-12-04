/**
 * 
 */
package com.sodo.xmarketing.service;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.sodo.xmarketing.dto.FundSearchDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.common.SearchResultMap;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.fund.Fund;
import org.springframework.data.domain.PageRequest;

/**
 * @author tuanhiep225
 *
 */
public interface FundService {

	/**
	 * @param fund
	 * @return
	 */
	Fund create(@Valid Fund fund);

	Map<String, Object> filterFund(String code,String type, String currency, String name, String managerCode,
			String country, String enabled, PageRequest request, String currentUserCode)
			throws SodException;

	Fund checkDuplicate(String fieldName, String value) throws SodException;

	SearchResultMap<Fund> getMapByFundGroupCode(FundSearchDTO fundSearch, int page, int size);

	List<Fund> suggestFund(String query, int numberRecord, String employeeCode) throws SodException;

	/**
	 * @param groupCode
	 * @param employeeCode
	 * @return
	 */
	List<Fund> getByGroupCodeAndEmployeeCode(String groupCode, String employeeCode);

}
