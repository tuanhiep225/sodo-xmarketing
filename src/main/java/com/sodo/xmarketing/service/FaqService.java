/**
 * 
 */
package com.sodo.xmarketing.service;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.support.FAQ;

/**
 * @author tuanhiep225
 *
 */
public interface FaqService {

	/**
	 * @return
	 */
	Collection<FAQ> getAll();

	/**
	 * @param entities
	 * @param currentUser
	 * @return
	 */
	SodResult<Collection<FAQ>> create(Collection<FAQ> entities, CurrentUser currentUser);

}
