package com.sodo.xmarketing.repository.fund;

import java.util.List;
import com.sodo.xmarketing.model.fund.Fund;
import com.sodo.xmarketing.repository.BaseRepository;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends BaseRepository<Fund, String>, FundCustomRepository {
    @Query("{'id':'?0','isDelete':false}")
    Fund getById(String id);

    @Query("{'code':'?0','isDelete':false}")
    Fund getByCode(String code);

    @Query("{'isDelete':false}")
    List<Fund> getFunds();

    @ExistsQuery("{'code':'?0','isDelete':false}")
    boolean existsByCode(String code);

}
