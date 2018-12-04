/**
 * 
 */
package com.sodo.xmarketing.repository.customer;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.Block;
import com.sodo.xmarketing.model.GroupService;
import com.sodo.xmarketing.repository.BaseRepository;

/**
 * @author tuanhiep225
 *
 */
@Repository
public interface GroupServiceRepository extends BaseRepository<GroupService, String>{

	@Query("{'code': ?0, 'isDelete':false}")
	GroupService getByCode(String code);

}
