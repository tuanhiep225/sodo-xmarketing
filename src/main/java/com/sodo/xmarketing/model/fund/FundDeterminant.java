/**
 * 
 */
package com.sodo.xmarketing.model.fund;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "fund-determinant")
public class FundDeterminant extends BaseEntity<String>{
	  @Id
	  private String id;

	  private String code;

	  @NotEmpty(message = "type is not empty")
	  private String type;

	  @NotEmpty(message = "parent is not empty")
	  private String parent;

	  @NotEmpty(message = "name is not empty")
	  @Size(min = 5, max = 100, message = "name character length from 5 to 100")
	  private String name;

	  @NotNull(message = "status is not empty")
	  private Boolean status;

	  private Boolean system = false;

	  @Size(max = 200, message = "note character length from 5 to 200")
	  private String note;

	  private int level;
	  // Mã cây định khoản 
	  private String treeCode;
}
