package com.sodo.xmarketing.utils;

public enum TranslateKey {

  NOT_ENOUGH_MONEY(1, "Không đủ tiền"),

  RESOLVED(2, "Đã xử lý "),

  COMPLEX(3, "Lỗi phức tạp ^^"),

  ORDER_CHANGE_STATUS(4, "đơn hàng chuyển trạng thái"),

  NO_TRANSLATE(5, "Không dịch"),

  TO(6, "sang"),

  NOTIFY_DEPOSIT(7, "Giao dịch cộng ví +{0} - {1} "),

  NOTIFY_WITHDRAWAL(8, "Giao dịch trừ ví -{0} - {1} "),

  DEPOSIT_CANCEL(9, ""),

  DEPOSIT_CANCEL_CONTENT(10, ""),

  DEPOSIT_ACCEPT(12, ""),

  DEPOSIT_ACCEPT_CONTENT(13, ""),

  WITHDRAWAL_CANCEL(11, ""),

  WITHDRAWAL_CANCEL_CONTENT(11, ""),

  WITHDRAWAL_ACCEPT(14, ""),

  WITHDRAWAL_ACCEPT_CONTENT(15, ""),

  REFUND_TICKET_SUCCESS(16, "Hoàn tiền khiếu nại thành công");


  private final int value;
  private final String reasonPhrase;

  private TranslateKey(int value, String reasonPhrase) {
    this.value = value;
    this.reasonPhrase = reasonPhrase;
  }

  public int value() {
    return value;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }
}
