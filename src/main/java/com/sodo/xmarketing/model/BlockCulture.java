/**
 * 
 */
package com.sodo.xmarketing.model;

import java.math.BigDecimal;

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
@NoArgsConstructor
@AllArgsConstructor
public class BlockCulture {
	private BigDecimal price; // giá này được tính cho khách hàng bình thường
	private BigDecimal wholesalePrices; // bản chất là VIP1
	private Integer denominator;// hệ số
	private String unitCurrency; // đơn vị tiền tệ
	private String unitName; // Tên đơn vị tính
	private String speed;
	private BigDecimal priceVip2; // thêm vào để tính giá cho những khách hàng được nâng lên VIP2

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getDenominator() {
		return denominator;
	}

	public void setDenominator(Integer denominator) {
		this.denominator = denominator;
	}

	public String getUnitCurrency() {
		return unitCurrency;
	}

	public void setUnitCurrency(String unitCurrency) {
		this.unitCurrency = unitCurrency;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public BigDecimal getWholesalePrices() {
		return wholesalePrices;
	}

	public void setWholesalePrices(BigDecimal wholesalePrices) {
		this.wholesalePrices = wholesalePrices;
	}
	
	
}