/**
 * 
 */
package com.sodo.xmarketing.repository.wallet;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.wallet.WalletDeterminant;
import com.sodo.xmarketing.repository.BaseRepository;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface WalletDeterminantRepository
		extends BaseRepository<WalletDeterminant, String>, WalletDeterminantCustomRepository {
	@Query(value = "{ 'isDelete' : false}")
	Page<WalletDeterminant> getWalletWithPagging(Pageable pageable);

	@Query(value = "{'code':?0 ,'isDelete' : false}")
	WalletDeterminant findByCode(String code);

	@Query(value = "{ 'isDelete' : false}")
	List<WalletDeterminant> getWalletDeterminantList();
}
