package com.sodo.xmarketing.utils;

public enum TransactionChainStatus {
  CREATED(1, "Tạo mới"), TRADING(2, "Đã duyệt"), COMPLETED(3, "Hoàn thành"), CANCEL(4, "Hủy");

  private final int value;

  private final String reasonPhrase;

  TransactionChainStatus(int value, String reasonPhrase) {
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
