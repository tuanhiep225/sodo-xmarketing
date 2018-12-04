/**
 * 
 */
package com.sodo.xmarketing.service.impl;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.voms.VOMSAttribute.FQAN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.common.SodResult;
import com.sodo.xmarketing.model.support.FAQ;
import com.sodo.xmarketing.repository.FAQRepository;
import com.sodo.xmarketing.service.FaqService;

/**
 * @author tuanhiep225
 *
 */
@Service
public class FaqServiceImpl implements FaqService {

	private static final Log LOGGER = LogFactory.getLog(FaqServiceImpl.class);
	
	@Autowired
	private FAQRepository faqRepo;

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.FaqService#get()
	 */
	@Override
	public Collection<FAQ> getAll() {
		// TODO Auto-generated method stub
		return faqRepo.getAll();
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.FaqService#create(java.util.Collection, com.sodo.xmarketing.model.account.CurrentUser)
	 */
	@Override
	public SodResult<Collection<FAQ>> create(Collection<FAQ> entities, CurrentUser currentUser) {
		if(currentUser == null)
			return SodResult.<Collection<FAQ>>builder().isError(true).code("CUSTOMER_INVALID").message("Customer invalid !").build();
		if(entities == null || entities.size() == 0)
		{
			return SodResult.<Collection<FAQ>>builder().isError(true).code("INPUT_VALID").message("Paramater entities must not be null or empty").build();
		}
		
		SodResult<Collection<FAQ>> result = new SodResult<Collection<FAQ>>();
		try {
			result.setResult(faqRepo.insert(entities));
		} catch (Exception e) {
			result.builder().isError(true).message(e.getMessage()).build();
		}
		return result;
	}
}
