package com.sodo.xmarketing.repository.config;

import com.sodo.xmarketing.model.config.Config;
import com.sodo.xmarketing.repository.BaseRepository;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author HenryDo
 * @created 27/10/2017 9:37 AM
 */

@Repository
public interface ConfigRepository extends BaseRepository<Config, String>, ConfigCustomRepository {

  @Query("{'key':'?0','isDelete':false}")
  Config findByKey(String key);

  @Query("{'key': {$regex:?0,$options:'i'}, 'isDelete': false}")
  List<Config> findByKeyClass(String className);
}
