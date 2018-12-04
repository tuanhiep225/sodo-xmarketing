/**
 * 
 */
package com.sodo.xmarketing.repository;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sodo.xmarketing.model.TransactionChain;

/**
 * @author tuanhiep225
 *
 */
public interface TransactionChainCustomRepository {
	TransactionChain updateField(String code, String fieldName, Boolean updatMultiField,
		      Map<String, Object> data);
}
