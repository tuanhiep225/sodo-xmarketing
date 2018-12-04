package com.sodo.xmarketing.utils;

public enum FunType {
  CASH(1, "Qũy tiền mặt"),
  BANK_ACCOUNT(2, "Quỹ tiền trong tài khoản"),
  ALIPAY(3, "Quỹ tiền thanh toán NCC");

  private final int value;

  private final String fundName;

  private FunType(int value, String fundName) {
    this.value = value;
    this.fundName = fundName;
  }

  public int value() {
    return value;
  }

  public String getFundName() {
    return fundName;
  }
}
