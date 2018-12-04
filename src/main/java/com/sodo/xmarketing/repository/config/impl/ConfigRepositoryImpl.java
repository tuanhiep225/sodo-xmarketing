package com.sodo.xmarketing.repository.config.impl;

import com.sodo.xmarketing.model.config.Config;
import com.sodo.xmarketing.repository.config.ConfigCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author HenryDo
 * @created 27/10/2017 9:43 AM
 */
public class ConfigRepositoryImpl implements ConfigCustomRepository {

  private final MongoTemplate mongoTemplate;

  @Autowired
  public ConfigRepositoryImpl(MongoTemplate mongoTemplate) throws Exception {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Config updateConfig(Config config) {
    Query query = new Query();
    query.addCriteria(Criteria.where("key").is(config.getKey()).and("isDelete").is(false));

    Update update = new Update();
    update.set("value", config.getValue());

    FindAndModifyOptions options = new FindAndModifyOptions();
    options.returnNew(true);
    options.upsert(true);

    return mongoTemplate.findAndModify(query, update, options, Config.class);
  }
}
