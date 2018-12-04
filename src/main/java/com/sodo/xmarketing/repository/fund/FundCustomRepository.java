package com.sodo.xmarketing.repository.fund;

import java.util.List;
import java.util.Map;
import com.sodo.xmarketing.dto.FundSearchDTO;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.fund.Fund;
import org.springframework.data.domain.Pageable;

public interface FundCustomRepository {

    Map<String, Object> filterFund(String code,String type, String currency, String name, String managerCode,
            String country, String enabled, Pageable pageable, String currentUserCode)
            throws SodException;

    List<Fund> suggestFund(String query, int numberRecord, String employeeCode) throws SodException;

    List<Fund> getByTypeAndCurrencyAndEmployeeCode(String[] type, String currency,
            String employeeCode);

    Fund checkDuplicate(String fieldName, String value) throws SodException;

    Map<String, Fund> getFromCodes(List<String> code);


    Fund updateField(String code, CurrentUser currentUser, String action, String fieldName,
            Map<String, Object> value);

    boolean updateFieldsFund(String code, Map<String, Object> mapFields, CurrentUser currentUser);

    SodSearchResult<Fund> getFundList(FundSearchDTO fundSearch, Pageable pageable);

    List<Fund> getFundByType();
    
	/**
	 * @param groupCode
	 * @param employeeCode
	 * @return
	 */
	List<Fund> getByGroupCodeAndEmployeeCode(String groupCode, String employeeCode);
}
