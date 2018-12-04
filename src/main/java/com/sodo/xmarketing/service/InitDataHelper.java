package com.sodo.xmarketing.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sodo.xmarketing.exception.SodException;
import com.sodo.xmarketing.model.Categories;
import com.sodo.xmarketing.model.account.CurrentUser;
import com.sodo.xmarketing.model.config.WalletDeter;
import com.sodo.xmarketing.model.fund.FundDeterminant;
import com.sodo.xmarketing.model.wallet.WalletDeterminant;
import com.sodo.xmarketing.status.SystemE;
import com.sodo.xmarketing.utils.AccountEntryContent;
import com.sodo.xmarketing.utils.ClassPathResourceReader;
import com.sodo.xmarketing.utils.ConfigHelper;
import com.sodo.xmarketing.utils.Properties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class InitDataHelper {

	private WalletDeter walletDeter;
	List<WalletDeterminant> walletDeterminants = new ArrayList();

	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Properties properties;


	public InitDataHelper(Properties properties, MongoTemplate mongoTemplate,
			ConfigHelper configHelper) {
		super();
		this.configHelper = configHelper;
		this.properties = properties;
		this.mongoTemplate = mongoTemplate;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public ConfigHelper getConfigHelper() {
		return configHelper;
	}

	public void setConfigHelper(ConfigHelper configHelper) {
		this.configHelper = configHelper;
	}

	public Boolean initdata(CurrentUser currentUser) throws SodException {
		// init like order
		addNewWalletDeterminant();

		return true;
	}

	public Boolean initFundDeterminant(CurrentUser currentUser) throws SodException {
		// init like order
		addNewFundDeterminant();

		return true;
	}

	String[] allFundDeterCodeX = {"DK_SUPER", "DK_DEPOSIT", "411", "801", "802", "808", "888",
			"889", "5111", "FUND_ADD_CASH", "FUND_ADD_CUSTOMER", "FUND_ADD_DELIVERY",
			"FUND_ADD_TRANSFER", "INTERNET_BANKING", "5112", "5112_1", "5112_2", "5112_3", "5112_4",
			"5112_5", "5113", "DK_WITHDRAWAL", "421", "156", "156_2", "156_3", "ALIPAY", "632",
			"632_1", "632_2", "632_3", "641", "641_1", "641_2", "641_3", "641_4", "641_5", "641_6",
			"641_7", "641_8", "642", "642_1", "642_1_1", "642_1_2", "642_1_3", "642_1_4", "642_2",
			"642_2_1", "642_2_2", "642_2_3", "642_2_4", "642_2_5", "642_3", "642_4", "642_5",
			"642_6", "642_7", "642_8", "901", "902", "902_1", "902_2", "902_3", "902_4", "902_5",
			"902_6", "331B", "909", "999", "FUND_SUBTRACT"};

	String[] allWalletDeterCodeX = {"DK_SUPER", "DK_DEPOSIT", "N1", "INTERNET_BANKING",
			"WALLET_ADD_CASH", "WALLET_ADD_CUSTOMER", "WALLET_ADD_DELIVERY", "WALLET_ADD_TRANSFER",
			"N2", "N3", "N6", "AFTER_COMPLETE", "ORDER_REFUND", "PRODUCT_CANCEL_REFUND", "N99",
			"SHOP_TICKET_REFUND", "TICKET_REFUND", "DEPOSIT_IN_ORDER", "DK_WITHDRAWAL",
			"DELIVERY_SHIP", "ORDER_STORAGE", "R2", "R3", "R6", "DELIVERY_APPROVED",
			"ORDER_DEPOSIT", "ORDER_DEPOSIT_ADD", "R7", "R99", "WALLET_SUBTRACT",
			"WITHDRAWAL_IN_ORDER"};

	private void addNewWalletDeterminant() {
		Gson gson = new Gson();
		String statisticFile = new ClassPathResourceReader("data/wallet.json").getContent();
		Map<String, Object> deter = gson.fromJson(statisticFile, HashMap.class);
		List<String> excudeValues = Arrays.asList("DK_SUPER");
		InitDataHelper initialDefaultData =
				new InitDataHelper(properties, mongoTemplate, configHelper);
		initialDefaultData.readChildWallet(deter, null);
		List<WalletDeterminant> walletDeterminants = initialDefaultData.walletDeterminants;
		List<WalletDeterminant> finalFundDeters =
				walletDeterminants.stream().filter(item -> !excudeValues.contains(item.getCode()))
						.collect(Collectors.toList());

		String s1 = "[";
		for (WalletDeterminant walletDeterminant : walletDeterminants) {
			s1 += "\"" + walletDeterminant.getCode() + "\",";
		}
		s1 += "]";

		System.out.println(s1);
		Query query = new Query();
		Criteria criteriaTreeCode = Criteria.where("code").in(allWalletDeterCodeX);
		query.addCriteria(criteriaTreeCode);

		mongoTemplate.remove(query, WalletDeterminant.class);
		mongoTemplate.insert(finalFundDeters, WalletDeterminant.class);

	}

	private void addNewFundDeterminant() {
		Gson gson = new Gson();
		String statisticFile = new ClassPathResourceReader("data/deter.vi.json").getContent();
		Map<String, Object> deter = gson.fromJson(statisticFile, HashMap.class);
		List<String> excudeValues = Arrays.asList("DK_SUPER");
		InitDataHelper initialDefaultData =
				new InitDataHelper(properties, mongoTemplate, configHelper);
		initialDefaultData.readChild(deter, null);
		List<FundDeterminant> fundDeterminants = initialDefaultData.fundDeterminants;

		List<FundDeterminant> finalFundDeters =
				fundDeterminants.stream().filter(item -> !excudeValues.contains(item.getCode()))
						.collect(Collectors.toList());

		String s1 = "[";
		for (FundDeterminant fundDeterminant : fundDeterminants) {
			s1 += "\"" + fundDeterminant.getCode() + "\",";
		}
		s1 += "]";
		System.out.println(s1);
		Query query = new Query();
		Criteria criteriaTreeCode = Criteria.where("code").in(allFundDeterCodeX);
		query.addCriteria(criteriaTreeCode);

		mongoTemplate.remove(query, FundDeterminant.class);
		mongoTemplate.insert(finalFundDeters, FundDeterminant.class);

	}


	List<FundDeterminant> fundDeterminants = new ArrayList();

	private void readChild(Map<String, Object> deter, Map<String, Object> parent) {
		Gson gson = new Gson();
		fundDeterminants.add(generateFundDeter(deter, parent));

		if (parent != null) {
			deter.put("treeCode",
					parent.get("treeCode").toString() + "." + deter.get("code").toString());
			deter.put("level", Integer.parseInt(parent.get("level").toString()) + 1);
			if (parent.containsKey("type")) {
				deter.put("type", parent.get("type").toString());
			}

		} else {
			deter.put("treeCode", deter.get("code").toString());
			deter.put("level", deter.get("level").toString());
		}
		if (deter.containsKey("children")) {
			Map<String, Object> childrenMap = (Map<String, Object>) deter.get("children");

			if (childrenMap.keySet() != null) {
				for (String string : childrenMap.keySet()) {
					Map<String, Object> tempData =
							gson.fromJson(gson.toJson(childrenMap.get(string)), HashMap.class);
					readChild(tempData, deter);
				}
			}
		}

	}

	private FundDeterminant generateFundDeter(Map<String, Object> deter,
			Map<String, Object> parent) {
		FundDeterminant result = new FundDeterminant();
		result.setCode(deter.get("code").toString());
		result.setName(deter.get("name").toString());
		result.setStatus(true);
		result.setSystem(false);
		result.setIsDelete(false);
		result.setTextSearch(result.getCode() + " " + result.getName());
		result.setSystem(true);

		if (parent != null) {
			result.setParent(parent.get("code").toString());
			result.setTreeCode(parent.get("treeCode").toString() + "." + result.getCode());
			result.setLevel(Integer.parseInt(parent.get("level").toString()) + 1);

		} else {
			result.setLevel(0);
			result.setTreeCode(deter.get("treeCode").toString());
		}
		if (!deter.get("code").toString().equals("ORG") && deter.get("type") == null
				&& parent != null) {
			result.setType(parent.get("type").toString());
		} else if (!deter.get("code").toString().equals("ORG") && deter.get("type") != null) {
			result.setType(deter.get("type").toString());
		}
		result.setCreatedBy(SystemE.SYSTEM_USERNAME.name());
		return result;
	}

	private void readChildWallet(Map<String, Object> deter, Map<String, Object> parent) {
		Gson gson = new Gson();
		walletDeterminants.add(generateWalletDeter(deter, parent));

		if (parent != null) {
			deter.put("treeCode",
					parent.get("treeCode").toString() + "." + deter.get("code").toString());
			deter.put("level", Integer.parseInt(parent.get("level").toString()) + 1);
			if (parent.containsKey("type")) {
				deter.put("type", parent.get("type").toString());
			}

		} else {
			deter.put("treeCode", deter.get("code").toString());
			deter.put("level", deter.get("level").toString());
		}
		if (deter.containsKey("children")) {
			Map<String, Object> childrenMap = (Map<String, Object>) deter.get("children");

			if (childrenMap.keySet() != null) {
				for (String string : childrenMap.keySet()) {
					Map<String, Object> tempData =
							gson.fromJson(gson.toJson(childrenMap.get(string)), HashMap.class);
					readChildWallet(tempData, deter);
				}
			}
		}

	}

	private WalletDeterminant generateWalletDeter(Map<String, Object> deter,
			Map<String, Object> parent) {
		WalletDeterminant result = new WalletDeterminant();
		result.setCode(deter.get("code").toString());
		result.setName(deter.get("name").toString());
		result.setStatus(true);
		result.setSystem(false);
		result.setIsDelete(false);
		result.setTextSearch(result.getCode() + " " + result.getName());
		result.setSystem(true);

		if (parent != null) {
			result.setParent(parent.get("code").toString());
			result.setTreeCode(parent.get("treeCode").toString() + "." + result.getCode());
			result.setLevel(Integer.parseInt(parent.get("level").toString()) + 1);

		} else {
			result.setLevel(0);
			result.setTreeCode(deter.get("treeCode").toString());
		}
		if (!deter.get("code").toString().equals("ORG") && deter.get("type") == null
				&& parent != null) {
			result.setType(parent.get("type").toString());
		} else if (!deter.get("code").toString().equals("ORG") && deter.get("type") != null) {
			result.setType(deter.get("type").toString());
		}
		result.setCreatedBy(SystemE.SYSTEM_USERNAME.name());
		return result;
	}

	/**
	 * 
	 */
	public void initAccountEntryContent() {
		JSONParser parser = new JSONParser();

		try {
			String statisticFile =
					new ClassPathResourceReader("data/resources.account_entry_content.vi.json")
							.getContent();
			Object obj = parser.parse(statisticFile);

			JSONObject jsonObject = (JSONObject) obj;

			Gson gson = new Gson();

			AccountEntryContent accountEntryContent =
					gson.fromJson(jsonObject.toJSONString(), AccountEntryContent.class);

			configHelper.saveConfig(accountEntryContent, AccountEntryContent.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initCategories() throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		String categoriesFile = new ClassPathResourceReader("data/categories.json").getContent();

		JsonNode categoriesNode = mapper.readTree(categoriesFile);

		JsonNode categoriesTenant = categoriesNode.get("vi");

		List<Categories> categories = mapper.readValue(categoriesTenant.toString(),
				new TypeReference<List<Categories>>() {});

		if (categories != null) {
			categories.forEach(category -> {
				Query query = new Query();
				query.addCriteria(Criteria.where("code").is(category.getCode()));
				if (mongoTemplate.findOne(query, Categories.class) == null) {
					mongoTemplate.insert(category, "categories");
				}
			});
		}
	}

}
