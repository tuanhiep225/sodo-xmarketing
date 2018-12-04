/**
 * 
 */
package com.sodo.xmarketing.service;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.wallet.WalletDeterminant;

/**
 * @author tuanhiep225
 *
 */
public interface WalletDeterminantService {
	WalletDeterminant create(WalletDeterminant wallet);

	List<WalletDeterminant> getWalletByPagging(Pageable pageable);

	Map<String, Object> getWalletByFilter(Boolean absoluteParent,String name, String parent, String type, Boolean status, Pageable pageable)
			throws SodException;

	WalletDeterminant update(WalletDeterminant wallet);

	WalletDeterminant findByCode(String code);

	WalletDeterminant findById(String id);

	List<WalletDeterminant> suggestWalletDeter(String query, int numberRecord) throws SodException;

	List<WalletDeterminant> getDeterminalTree(int maxLevel, boolean containSystem) throws SodException;

	WalletDeterminant checkDuplicate(String fieldName, String value) throws SodException;

	Map<String, WalletDeterminant> getWalletDeterminantsFromCodes(List<String> code) throws SodException;

	Map<String, String> getWalletDeterminantsName();

	WalletDeterminant delete(String code) throws SodException;
}
