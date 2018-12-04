package com.sodo.xmarketing.model.wallet;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Henry Do User: henrydo Date: 15/08/2018 Time: 14/43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet implements Serializable {

  // Mã khách hàng
  private String customerCode;

  // Tài khoản khách hàng
  private String customerUserName;

  // Tên khách hàng
  private String customerName;

  // Email khách hàng
  private String customerEmail;
}
