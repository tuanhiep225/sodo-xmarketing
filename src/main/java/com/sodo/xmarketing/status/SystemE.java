package com.sodo.xmarketing.status;

public enum SystemE {

  SYSTEM_NAME(1, "Hệ thống"),
  SYSTEM_CODE(2, "Mã hệ thống"),
  SYSTEM_USERNAME(3,"Tên đăng nhập hệ thống"),
  SYSTEM_PASSWORD(4,"Password hệ thống"),
  DK_DEPOSIT(2, "Nạp quỹ"),
  DK_WITHDRAWAL(3, "Trừ quỹ"),
  DK_SUPER(0, "Super"),
  DK_ORIGIN(1, "Origin"),
  SALE_NAME(1,"Sale name"),
  SALE_CODE(1,"Sale system code"),
  SALE(3,"Sale username");
  
  private final int value;

  private final String reasonPhrase;

  private SystemE(int value, String reasonPhrase) {
    this.value = value;
    this.reasonPhrase = reasonPhrase;
  }

  public int value() {
    return value;
  }

  public String getLabel() {
    return reasonPhrase;
  }
}
