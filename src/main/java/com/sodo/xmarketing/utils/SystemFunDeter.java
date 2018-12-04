package com.sodo.xmarketing.utils;

import java.util.Arrays;
import java.util.List;

public enum SystemFunDeter {
  TRANSFER_SAME_CRC("902_1", "Chuyển quỹ cùng đơn vị tiền tệ"),

  TRANSFER_DIF_CRC("902_2", "Chuyển quỹ khác đơn vị tiền tệ"),

  TRANSFER_TARGET_FUND("802", "Chuyển quỹ khác đơn vị tiền tệ"),

  TRANSFER_FEE("902_4", "Phí chuyển khoản chuyển quỹ"),

  FUND_SUBTRACT("331", "Chi theo yêu cầu rút ví"),

  FUND_ADD_CUSTOMER("FUND_ADD_CUSTOMER","Nạp quỹ theo yêu cầu check nạp ví"),
  
  BUY_CNY("902_5","Mua tệ")
  ;

  // Quỹ cùng đơn dị tiền tệ, quỹ khác đơn vị tiền tệ, quỹ công nợ
  public static final List<String> TRANSFER_FUND = Arrays.asList("902_1", "902_2","902_3","902_5");
  private final String value;

  private final String deterCode;

  SystemFunDeter(String value, String deterCode) {
    this.value = value;
    this.deterCode = deterCode;
  }


  public String getValue() {
    return value;
  }

  public String getDeterCode() {
    return deterCode;
  }


}
