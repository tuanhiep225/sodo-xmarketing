/**
 *
 */
package com.sodo.xmarketing.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.model.wallet.Determinant;

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
public class FundTransactDTO implements Serializable {

  private String fundCode;
  private String fundName;
  private Determinant determinant;
  private String content;
  private Format format;
  private BigDecimal exchangeRate;
  private String employeeCreate;
}
