/**
 * 
 */
package com.sodo.xmarketing.service.impl;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.wallet.WalletDeterminant;
import com.sodo.xmarketing.model.wallet.WalletTransaction;
import com.sodo.xmarketing.repository.wallet.WalletDeterminantRepository;
import com.sodo.xmarketing.repository.wallet.WalletTransactionRepository;
import com.sodo.xmarketing.service.WalletDeterminantService;
import static com.sodo.xmarketing.utils.DateUtils.*;

/**
 * @author tuanhiep225
 *
 */
@Service
public class WalletDeterminantServiceImpl implements WalletDeterminantService {

	private static final Log LOGGER = LogFactory.getLog(WalletDeterminantServiceImpl.class);
	
	  @Autowired
	  WalletDeterminantRepository walletRepository;
	  @Autowired
	  NextSequenceService sequenceService;
	  
	  @Autowired
	  WalletTransactionRepository walletTransactionRepository;

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#create(com.sodo.xmarketing.model.wallet.WalletDeterminant)
	 */
	@Override
	public WalletDeterminant create(WalletDeterminant wallet) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#getWalletByPagging(org.springframework.data.domain.Pageable)
	 */
	@Override
	public List<WalletDeterminant> getWalletByPagging(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#getWalletByFilter(java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Map<String, Object> getWalletByFilter(Boolean absoluteParent, String name, String parent, String type,
			Boolean status, Pageable pageable) throws SodException {
		 if (!absoluteParent && !isNullOrEmpty(parent)) {
		      WalletDeterminant walletDeterminant = walletRepository.findByCode(parent);
		      return walletRepository.getWalletByFilter(absoluteParent, name,
		          walletDeterminant.getTreeCode(), type, status, pageable);
		    } else {
		      return walletRepository.getWalletByFilter(absoluteParent, name, parent, type, status,
		          pageable);

		    }
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#update(com.sodo.xmarketing.model.wallet.WalletDeterminant)
	 */
	@Override
	public WalletDeterminant update(WalletDeterminant wallet) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#findByCode(java.lang.String)
	 */
	@Override
	public WalletDeterminant findByCode(String code) {
		return walletRepository.findByCode(code);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#findById(java.lang.String)
	 */
	@Override
	public WalletDeterminant findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#suggestWalletDeter(java.lang.String, int)
	 */
	@Override
	public List<WalletDeterminant> suggestWalletDeter(String query, int numberRecord) throws SodException {
		return walletRepository.suggestWalletDeter(query, numberRecord);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#getDeterminalTree(int, boolean)
	 */
	@Override
	public List<WalletDeterminant> getDeterminalTree(int maxLevel, boolean containSystem) throws SodException {
		 return walletRepository.getDeterminalTree(maxLevel, containSystem);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#checkDuplicate(java.lang.String, java.lang.String)
	 */
	@Override
	public WalletDeterminant checkDuplicate(String fieldName, String value) throws SodException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#getWalletDeterminantsFromCodes(java.util.List)
	 */
	@Override
	public Map<String, WalletDeterminant> getWalletDeterminantsFromCodes(List<String> code) throws SodException {
		 return walletRepository.getWalletDeterminantsFromCodes(code);
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#getWalletDeterminantsName()
	 */
	@Override
	public Map<String, String> getWalletDeterminantsName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sodo.xmarketing.service.WalletDeterminantService#delete(java.lang.String)
	 */
	@Override
	public WalletDeterminant delete(String code) throws SodException {
		// TODO Auto-generated method stub
		return null;
	}
}
