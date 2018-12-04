package com.sodo.xmarketing.model.config;

import com.sodo.xmarketing.model.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author HenryDo
 * @created 27/10/2017 9:33 AM
 */
@Data
@Document(collection = "config")
public class Config extends BaseEntity<String> {

  @Id
  private String id;
  private String key;
  private String value;

  public Config() {
  }

  public Config(String key, String value) {
    this.key = key;
    this.value = value;
  }
}
