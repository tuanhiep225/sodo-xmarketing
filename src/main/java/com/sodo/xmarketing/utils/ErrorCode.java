package com.sodo.xmarketing.utils;

public enum ErrorCode {

  // @formatter:off
  NOT_FOUND_CUSTOMER(1, "Không tìm thấy khách hàng"),

  CANNOT_UPDATE_CUSTOMER(2, "Không thể update khách hàng"),

  NEGATIVE_TRANSACTION_VALUE(3, "Không tìm thấy khách hàng"),

  NOT_FOUND_DEPOSIT(4, "Không tìm thấy yêu cầu check nạp tiền"),

  NOT_FOUND_WITHDRAWAL(5, "Không tìm thấy yêu cầu rút tiền"),

  NOT_FOUND_ACCOUNTING_ENTRY(6, "Không tìm thấy phiếu giao dịch ví"),

  NOT_ENOUGH_MONEY(7, "Không đủ tiền trong tài khoản"),

  NOT_ENOUGH_MONEY_NEGATIVE(8, "Không đủ tiền trong tài khoản từ âm"),

  RESOLVED(9, "Đã xử lý"),

  NOT_FOUND_TICKET(10, "Không tìm thấy ticket"),

  NOT_FOUND_ORDER(11, "Không tìm thấy order"),

  TICKET_RECEIVED(12, "Khiếu nại đã được tiếp nhận"),

  NOT_PERMISSION(13, "Không có quyền truy cập"),

  EXIST_CUSTOMER_OWNED(14, "Tồn tại khách hàng"),

  NOT_HAVE_ID_PATH(15, "Không có mã idPath"),

  PERMISSION_CHANGE_OWNER(16, "Không có quyền sở hữu"),

  TOO_MUCH_TICKET(17, "Quá nhiều ticket, không thể nhận thêm"),

  UNIT_DIFFERENT_TYPE(18, "Cannot update Unit with  different type"),

  EXPIRED_DATE_UPDATE(19, "Expiry date to update object"),

  NOT_FOUND_EMPLOYEE(20, "Not found employee"),

  MISSING_INFO(21, "Thieu truong thong tin"),

  UNIT_TYPE_NOT_MATCH(22, "Kiểu đơn vị không phù hợp"),

  CANNOT_CREATE(23, "Không thể tạo"),

  CANNOT_UPDATE(24, "Không thể tạo"),

  NOT_FOUND_FUND(25, "Không tìm thấy quỹ"),

  DELETED(26, "Đã bị xóa"),

  DETERMINAL_USING(27, "Định khoản đang được sử dụng"),

  MISSING_DATA(28, "Thiếu dữ liệu"),

  PASSWORD_NOTMATCH(29, "2 pass không trùng khớp"),

  CURRENT_PASSWORD_NOTMATCH(30, "Mật khẩu hiện tại không trùng khớp"),

  DUPLICATE_ACCOUNT_SMS(31, "Trùng  Account SMS"),

  WRONG_ORDER_TRANSACTION(32, "Thứ tự thực hiện giao dịch không đúng"),

  TRANSACTION_CHAIN_CANNOT_CANCEL(33, "Không thể hủy chuỗi giao dịch"),

  ERROR_EXISTS(32, "Không tồn tại bản ghi"),

  ERROR_DATA(33, "Dữ liệu không hợp lệ"),

  ORDER_STATUS_CANNOT_COMPLAIN(34, "Không thể tạo khiếu nại");
  // @formatter:on


  private final int value;
  private final String reasonPhrase;

  private ErrorCode(int value, String reasonPhrase) {
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
