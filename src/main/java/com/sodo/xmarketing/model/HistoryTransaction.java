/**
 * 
 */
package com.sodo.xmarketing.model;
import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
public class HistoryTransaction {


	  private String id;
	  private String content;

	  @CreatedDate
	  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
	  @JsonSerialize(using = LocalDateTimeSerializer.class)
	  @Indexed(direction = IndexDirection.DESCENDING)
	  private LocalDateTime createdDate;

	  private String createdBy;
	  public HistoryTransaction(String content, LocalDateTime createdDate, String createdBy) {
		    this.content = content;
		    this.createdDate = createdDate;
		    this.createdBy = createdBy;
		  }
}
