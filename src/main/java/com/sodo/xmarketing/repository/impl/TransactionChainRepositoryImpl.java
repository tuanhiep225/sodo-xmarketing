/**
 * 
 */
package com.sodo.xmarketing.repository.impl;

import java.util.Map;
import com.sodo.xmarketing.model.TransactionChain;
import com.sodo.xmarketing.repository.TransactionChainCustomRepository;
import com.sodo.xmarketing.service.impl.NextSequenceService;
import com.sodo.xmarketing.utils.AccFunctionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author tuanhiep225
 *
 */
public class TransactionChainRepositoryImpl implements TransactionChainCustomRepository {

	@Autowired
	NextSequenceService sequenceService;

	@Autowired
	private MongoTemplate mongoTemplate;

	private AccFunctionUtil accFunctionUtil;


	@Override
	public TransactionChain updateField(String code, String fieldName, Boolean updatMultiField,
			Map<String, Object> data) {
		Update update = new Update();
		Query query = new Query().addCriteria(Criteria.where("code").is(code));

		if (updatMultiField) {
			for (String field : data.keySet()) {
				if (!field.equalsIgnoreCase("historyTransaction")) {
					update.set(field, data.get(field));
				} else {
					update.push(field, data.get(field));
				}

			}
		} else {
			update.set(fieldName, data.get(fieldName));
		}
		mongoTemplate.updateFirst(query, update, TransactionChain.class);

		return mongoTemplate.findOne(query, TransactionChain.class);
	}
}
