/**
 *
 */
package com.sodo.xmarketing.model;

import com.querydsl.core.annotations.QueryEntity;
import com.sodo.xmarketing.model.entity.BaseEntity;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author tuanhiep225
 */

@QueryEntity
@Document(collection = "group-service")
public class GroupService extends BaseEntity<String> {

  private Map<String, GroupServiceCulture> culture;

  @Indexed
  @NotEmpty
  private String code;
  private String id;

  public Map<String, GroupServiceCulture> getCulture() {
    return culture;
  }

  public void setCulture(Map<String, GroupServiceCulture> culture) {
    this.culture = culture;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
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

class GroupServiceCulture {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
