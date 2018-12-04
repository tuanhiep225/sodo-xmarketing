/**
 * 
 */
package com.sodo.xmarketing.model.account;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Unique;

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
@Document(collection="role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity<String>{

	private static final Log LOGGER = LogFactory.getLog(Role.class);
	

	  @Id
	  private transient String id;

	  @Unique
	  private String name;

	  @Unique
	  private String code;

	  private Set<String> permissions;

	  private transient List<Action> actions;

	  private boolean activated;
	  


	  @Override
	  public String toString() {
	    return name;
	  }


	  @Override
	  public String getId() {
	    return id;
	  }

	  @Override
	  public void setId(String id) {
	    this.id = id;
	  }
}
