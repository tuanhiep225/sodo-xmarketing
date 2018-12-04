/**
 *
 */
package com.sodo.xmarketing.model;

import java.math.BigDecimal;
import java.util.Map;

import com.sodo.xmarketing.model.ServicePriceCulture.ServicePriceCultureBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Block {

  private Map<String, BlockCulture> culture;
  private String id;

  public Map<String, BlockCulture> getCulture() {
    return culture;
  }

  public void setCulture(Map<String, BlockCulture> culture) {
    this.culture = culture;
  }

}
