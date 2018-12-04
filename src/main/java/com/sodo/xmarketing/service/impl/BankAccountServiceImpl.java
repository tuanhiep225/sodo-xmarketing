package com.sodo.xmarketing.service.impl;

import com.sodo.xmarketing.model.bank.BankAccount;
import com.sodo.xmarketing.repository.bank.BankAccountRepository;
import com.sodo.xmarketing.service.BankAccountService;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Created by Henry Do User: henrydo Date: 16/08/2018 Time: 09/48
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

  private final BankAccountRepository repository;

  public BankAccountServiceImpl(
      BankAccountRepository repository) {
    this.repository = repository;
  }

  /**
   * Lấy ra các tài khoản ngân hàng theo ngôn ngữ và loại tiền tệ
   * @param lang Ngôn ngữ mặc định
   * @param currencyCode Loại tiền tệ
   * @return Danh sách các tài khoản
   */
  @Override
  public List<BankAccount> findByLangAndCurrency(String lang, String currencyCode) {
    return repository.findByLangAndCurrencyCodeAndIsDeleteIsFalse(lang, currencyCode);
  }
}
