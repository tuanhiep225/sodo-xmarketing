/**
 *
 */
package com.sodo.xmarketing.repository.fund;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.fund.FundDeterminant;

/**
 * @author ANH MINH - PC
 */
public interface FundDeterminantCustomRepository {

  Map<String, Object> getFundByFilter(Boolean absoluteParent, String name, String parent,
      String type, Boolean status, Pageable pageable) throws SodException;

  List<FundDeterminant> suggestFundDeter(String query, int numberRecord) throws SodException;

  List<FundDeterminant> getDeterminalTree(int maxLevel, boolean containSystem) throws SodException;

  FundDeterminant checkDuplicate(String fieldName, String value) throws SodException;

  Map<String, FundDeterminant> getFundDeterminantsFromCodes(List<String> code);

  List<FundDeterminant> getListTreeDeter(List<String> treeCodes);
}
