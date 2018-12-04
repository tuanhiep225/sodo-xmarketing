/**
 *
 */
package com.sodo.xmarketing.service.impl;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.google.common.base.Strings;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Sequences;
import com.sodo.xmarketing.utils.GenerateValueIdentifier;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @author tuanh
 */
@Service
public class NextSequenceService {

	@Autowired
	private MongoOperations mongo;

	private int getNextSequence(String seqName) {
		return getSequence(seqName);
	}

	private int getSequence(String seqName) {
		Sequences counter =
				mongo.findAndModify(query(where("_id").is(seqName)), new Update().inc("seq", 1),
						options().returnNew(true).upsert(true), Sequences.class);
		return counter.getSeq();
	}

	public String genCustomerCode(String prefix) throws SodException {
		if (Strings.isNullOrEmpty(prefix)) {
			return GenerateValueIdentifier.generate(getNextSequence("customer-code"));
		}

		return prefix + GenerateValueIdentifier.generate(getNextSequence("customer-code"));
	}

	public String genWalletTransactionCode() {
		var dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		return getNextSequence(dateNow) + dateNow;
	}

	public String genOrderCode(String customerCode) throws SodException {
		if (Strings.isNullOrEmpty(customerCode)) {
			return "ORDER" + getNextSequence("ORDER");
		}

		return customerCode + "-" + getNextSequence(customerCode);
	}

	public String genEmployeeCode() throws SodException {

		return "E" + GenerateValueIdentifier.generate(getNextSequence("employee-code"));
	}

	public String genCategories() {
		return "CAT" + getNextSequence("categories");
	}

	public String genFundCode() {
		return "Q_" + getNextSequence("fundcode");
	}
	
	public String genOrderDistributorCode() {
		return "NCC_" + getNextSequence("orderDistributorCode");
	}

	/**
	 * @return
	 */
	public String genFundTransactCode() {
		 return "QDQ-" + getNextSequence("fundTransactcode");
	}

	/**
	 * @return
	 * @throws SodException 
	 */
	public String genDepositCode() throws SodException {
		return GenerateValueIdentifier.generateCode("YC_", getNextSequence("deposit"));
	}

	/**
	 * @return
	 */
	public String genTransactionChaninCode() {
		 return "TC" + Integer.toString(getNextSequence("transactionChain"));
	}
}
