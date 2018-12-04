/**
 * 
 */
package com.sodo.xmarketing.repository.fund;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.repository.BaseRepository;


/**
 * @author tuanhiep225
 *
 */
@Repository
public interface FundDeterminantRepository extends BaseRepository<FundDeterminant, String>, FundDeterminantCustomRepository{

	  @Query(value = "{ 'isDelete' : false}")
	  Page<FundDeterminant> getFundWithPagging(Pageable pageable);

	  FundDeterminant findByCode(String code);

	  @Query(value = "{ 'isDelete' : false}")
	  List<FundDeterminant> getFundDeterminant();
}
