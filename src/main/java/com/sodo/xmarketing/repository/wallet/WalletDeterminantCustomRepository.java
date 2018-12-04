/**
 * 
 */
package com.sodo.xmarketing.repository.wallet;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Pageable;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.wallet.WalletDeterminant;

/**
 * @author tuanhiep225
 *
 */
public interface WalletDeterminantCustomRepository {
	Map<String, Object> getWalletByFilter(Boolean absoluteParent, String name, String parent, String type,
			Boolean status, Pageable pageable) throws SodException;

	List<WalletDeterminant> suggestWalletDeter(String query, int numberRecord) throws SodException;

	List<WalletDeterminant> getDeterminalTree(int maxLevel, boolean containSystem) throws SodException;

	WalletDeterminant checkDuplicate(String fieldName, String value) throws SodException;

	Map<String, WalletDeterminant> getWalletDeterminantsFromCodes(List<String> code);
}
