package com.sodo.xmarketing.model.config;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 09/53
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Format implements Serializable {

  // Ngôn ngữ
  private String lang;
  // Kí tự phân cách hàng nghìn
  private String thousands;
  // Kí tự phân cách hàng thập phân
  private String decimal;
  // Định dạng format ngày/tháng/năm
  private String date;
  // Định dạng formatInt
  private String numberInt;
  // Định dạng format số Float
  private String numberFloat;
  // Tên hiển thị tiền tệ
  private String currencyName;
  // Định dạng kiểu tiền tệ
  private String currencyFormat;
  // Phân cách thông số
  private int currencyPrecision;
  // Mã quốc gia
  private String countryCode;
  // Mã tiền tệ
  private String currency;
}
