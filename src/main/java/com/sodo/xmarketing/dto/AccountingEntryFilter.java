/**
 *
 */
package com.sodo.xmarketing.dto;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anhmi
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountingEntryFilter {

  private String userName;

  private String transactionType;

  private String walletDeterminantCode;

  private String code;

  private String status;

  private Boolean absoluteDeter;

  private BigDecimal amountFrom;

  private BigDecimal amountTo;

  private String startDate;

  private String endDate;

  private String orderCode;

}
