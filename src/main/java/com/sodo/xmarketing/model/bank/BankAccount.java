package com.sodo.xmarketing.model.bank;

import com.sodo.xmarketing.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Henry Do User: henrydo Date: 16/08/2018 Time: 09/31
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bank-account")
public class BankAccount extends BaseEntity<String> {

  @Id
  private String id;

  // Ngôn ngữ, culture của tài khoản
  private String lang;

  // Đơn vị tiền tệ của tài khoản
  private String currencyCode;

  // Tên ngân hàng
  private String bankName;

  // Số tài khoản ngân hàng
  private String number;

  // Chủ sở hữ
  private String memberName;

  // Tên chi nhánh
  private String branchName;

  // Mô tả về tài khoản
  private String description;
}
