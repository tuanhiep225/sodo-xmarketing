package com.sodo.xmarketing.controller;

import com.sodo.xmarketing.model.bank.BankAccount;
import com.sodo.xmarketing.service.BankAccountService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 11/16
 */
@RestController
@RequestMapping("/api/v1/bank-accounts")
public class BankAccountController {


  private final BankAccountService service;

  public BankAccountController(BankAccountService service) {
    this.service = service;
  }

  /**
   * @Henry Lẩy ra các tài khoản ngân hàng
   */
  @GetMapping("/")
  public List<BankAccount> getByLangAndCurrency(
      @RequestParam String lang,
      @RequestParam String currencyCode
  ) {
    return service.findByLangAndCurrency(lang, currencyCode);
  }
}
