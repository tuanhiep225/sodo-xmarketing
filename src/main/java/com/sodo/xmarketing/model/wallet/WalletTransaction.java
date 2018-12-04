package com.sodo.xmarketing.model.wallet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sodo.xmarketing.dto.FundTransactDTO;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.entity.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Henry Do User: henrydo Date: 15/08/2018 Time: 14/45
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "wallet-transaction")
public class WalletTransaction extends BaseEntity<String> {

  @Id
  private String id;

  // Mã giao dịch
  private String code;

  // Ngày chứng từ
  @JsonFormat(pattern = "dd/MM/yyyy")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate date;

  // Ví giao dịch
  private Wallet wallet;

  // Target đến đối tượng
  private TargetObject target;

  // Số tiền ví trước giao dịch
  private BigDecimal beforeAmount;

  // Số tiền giao dịch
  private BigDecimal amount;

  // Số tiền ví sau giao dịch
  private BigDecimal afterAmount;

  // Loại giao dịch (UP: Cộng tiền, DOWN: Trừ tiền)
  private TransactionType type;

  // Trạng thái giao dịch
  private TransactionStatus status;

  // Nội dung giao dịch
  private String content;

  // Mã định khoản ví
  private Determinant walletDeterminant;

  private String treeCode;

  private Format format;
  
  private String accountantAccept;
  
  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime timeAccept;


  private FundTransactDTO fundTransact;
  
  private String transactionChainCode;
  
  private String depositCode; // mã yêu cầu checknap ví
}
