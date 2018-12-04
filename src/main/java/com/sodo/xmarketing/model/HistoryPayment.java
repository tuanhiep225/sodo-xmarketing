/**
 * 
 */
package com.sodo.xmarketing.model;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.model.entity.BaseEntity;

/**
 * @author tuanhiep225
 *
 */

@QueryEntity
@Document(collection="history-payment")
public class HistoryPayment extends BaseEntity<String>{

	private String id;
	private Order order;
	private BigDecimal totalPrice; // price affer discount: totalPrice= price -discount
	private BigDecimal discount;
	private String username; //
	private BigDecimal price; // price for order
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id= id;
		
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	
}
