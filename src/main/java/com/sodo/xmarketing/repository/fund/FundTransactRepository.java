/**
 * 
 */
package com.sodo.xmarketing.repository.fund;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.fund.FundTransaction;
import com.sodo.xmarketing.repository.BaseRepository;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface FundTransactRepository extends BaseRepository<FundTransaction, String>, FundTransactCustomRepository {
	  @Query("{'id':'?0','isDelete':false}")
	  FundTransaction getById(String id);

	  @Query("{'code':'?0','isDelete':false}")
	  FundTransaction getByCode(String code);
}
