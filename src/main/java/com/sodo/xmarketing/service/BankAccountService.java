package com.sodo.xmarketing.service;

import com.sodo.xmarketing.model.bank.BankAccount;
import java.util.List;

/**
 * Created by Henry Do User: henrydo Date: 16/08/2018 Time: 09/48
 */
public interface BankAccountService {

  List<BankAccount> findByLangAndCurrency(String lang, String currencyCode);
}
