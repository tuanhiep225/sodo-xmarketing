/**
 * 
 */
package com.sodo.xmarketing.dto;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearch {

   private String orderCode;
   private String username;
   private String staffCode;
   private String saleCode;
   private String startDate;
   private String endDate;
   private BigDecimal fromValue;
   private BigDecimal toValue;
   private String serviceCode;
   private String url;
   private String distributorCode;
   private String status;
}
