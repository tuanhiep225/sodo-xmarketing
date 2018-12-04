/**
 * 
 */
package com.sodo.xmarketing.service;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.PageRequest;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.fund.FundDeterminant;

/**
 * @author tuanhiep225
 *
 */
public interface FundDeterminantService {

	/**
	 * @param absoluteParent
	 * @param name
	 * @param parent
	 * @param type
	 * @param status
	 * @param request
	 * @return
	 * @throws SodException 
	 */
	Map<String, Object> getFundByFilter(Boolean absoluteParent, String name, String parent, String type, Boolean status,
			PageRequest request) throws SodException;

	/**
	 * @param code
	 * @return
	 */
	FundDeterminant findByCode(String code);

	/**
	 * @param lowerCase
	 * @param numberRecord
	 * @return
	 * @throws SodException 
	 */
	List<FundDeterminant> suggestFundDeter(String lowerCase, int numberRecord) throws SodException;

	/**
	 * @param maxLevel
	 * @param containSystem
	 * @return
	 * @throws SodException 
	 */
	List<FundDeterminant> getDeterminalTree(int maxLevel, boolean containSystem) throws SodException;

	/**
	 * @param codes
	 * @return
	 */
	Map<String, FundDeterminant> getFundDeterminantsFromCodes(List<String> codes);

	/**
	 * @param treeCodes
	 * @return
	 */
	List<FundDeterminant> getListTreeDeter(List<String> treeCodes);

	/**
	 * @param currentUser
	 * @throws SodException 
	 */
	void initData(CurrentUser currentUser) throws SodException;

}
