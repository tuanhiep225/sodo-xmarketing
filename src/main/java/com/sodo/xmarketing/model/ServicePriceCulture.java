/**
 * 
 */
package com.sodo.xmarketing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePriceCulture{
	private String name;
	private Integer miniumOrder;
	private String description;
	private Integer minMinute;
	private Integer maxOrder;
	private Boolean allowTrial = false;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getMiniumOrder() {
		return miniumOrder;
	}
	public void setMiniumOrder(Integer miniumOrder) {
		this.miniumOrder = miniumOrder;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}