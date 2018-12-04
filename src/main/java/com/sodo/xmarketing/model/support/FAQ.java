/**
 * 
 */
package com.sodo.xmarketing.model.support;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.model.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tuanhiep225
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@QueryEntity
@Document(collection ="faq")
public class FAQ extends BaseEntity<String>{
	public String id;
	public Map<String, FAQCulture> culture;
}