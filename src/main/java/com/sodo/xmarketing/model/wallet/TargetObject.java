package com.sodo.xmarketing.model.wallet;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Henry Do User: henrydo Date: 15/08/2018 Time: 15/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetObject implements Serializable {
  // Mã đối tượng
  private String code;

  // Loại đối tượng (Khách hàng: Rút tiền, nạp tiền vào ví, Đơn hàng: Thực hiện giao dịch trên đơn
  // hàng.)
  private TargetObjectType type;
}
