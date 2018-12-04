/**
 * 
 */
package com.sodo.xmarketing.repository;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.account.Deposit;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface DepositRepository extends BaseRepository<Deposit, String>, DepositCustomRepository {
	void removeById(String id);

	@Query(value = "{ 'isDelete' : false}")
	Page<Deposit> getAllDeposit(Pageable pageable);

	@Query(value = "{'code': ?0, 'isDelete' : false}")
	Deposit getByCode(String code);

}
