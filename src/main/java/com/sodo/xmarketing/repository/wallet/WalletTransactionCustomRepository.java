/**
 * 
 */
package com.sodo.xmarketing.repository.wallet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.dto.AccountingEntryFilter;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodSearchResult;
import com.sodo.xmarketing.model.wallet.WalletTransaction;

/**
 * @author tuanhiep225
 *
 */
public interface WalletTransactionCustomRepository {
	/**
	 * @param data
	 * @param page
	 * @param size
	 * @return
	 */
	SodSearchResult<WalletTransaction> filterAccounting(AccountingEntryFilter data, int page, int size);
	
	/**
	 * @param querry
	 * @param numberRecord
	 * @return
	 */
	List<WalletTransaction> suggestAccountingEntryCode(String querry, int numberRecord);
	
	
	WalletTransaction updateField(String code, CurrentUser currentUser, String action, String fieldName,
		      Map<String, Object> value);

}
