package com.sodo.xmarketing.repository.bank;

import com.sodo.xmarketing.model.bank.BankAccount;
import com.sodo.xmarketing.repository.BaseRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Created by Henry Do User: henrydo Date: 16/08/2018 Time: 09/45
 */
@Repository
public interface BankAccountRepository extends BaseRepository<BankAccount, String> {

  List<BankAccount> findByLangAndCurrencyCodeAndIsDeleteIsFalse(String lang, String currencyCode);
}
