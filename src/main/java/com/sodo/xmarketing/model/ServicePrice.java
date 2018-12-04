/**
 * 
 */
package com.sodo.xmarketing.model;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.model.ServicePriceCulture.ServicePriceCultureBuilder;
import com.sodo.xmarketing.model.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */
@QueryEntity
@Document(collection = "service-price")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePrice extends BaseEntity<String>{
	
	private Map<String, ServicePriceCulture> culture;
	
	@Indexed
	@NotEmpty
	private String groupServiceCode;
	
	@Indexed
	@NotEmpty
	private String code;
	
	private Block block;
	private String icon;
	String id;
	
	private Boolean isException = false;


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Map<String, ServicePriceCulture> getCulture() {
		return culture;
	}

	public void setCulture(Map<String, ServicePriceCulture> culture) {
		this.culture = culture;
	}

	public String getGroupServiceCode() {
		return groupServiceCode;
	}

	public void setGroupServiceCode(String groupServiceCode) {
		this.groupServiceCode = groupServiceCode;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
		
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
	
	
}

