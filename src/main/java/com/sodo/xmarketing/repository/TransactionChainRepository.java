/**
 * 
 */
package com.sodo.xmarketing.repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.TransactionChain;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface TransactionChainRepository extends BaseRepository<TransactionChain, String>, TransactionChainCustomRepository{
	  @Query("{'id':'?0','isDelete':false}")
	  TransactionChain getById(String id);

	  @Query("{'code':'?0','isDelete':false}")
	  TransactionChain getByCode(String code);
}
