package com.sodo.xmarketing.model.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sodo.xmarketing.dto.StaffDTO;
import com.sodo.xmarketing.model.account.Account;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.status.CustomerStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends Account {

  // Tên hiển thị
  private String name;

  // Số dư hiện tại
  private BigDecimal balance = BigDecimal.ZERO;

  // Sô tiền đã sử dụng dịch vụ
  private BigDecimal balanceLife = BigDecimal.ZERO;

  // Số điện thoại
  private String phone;

  // Kiểu tiền tệ mặc định
  @NotNull
  private Format format;
  
  @JsonFormat(pattern = "yyyy-MM-dd")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate dateOfBirth;
  
  private String address;
  
  CustomerStatus attribute;
  // Xác định nhân viên sale;
  StaffDTO sale;
  
}
