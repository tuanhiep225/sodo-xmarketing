/**
 *
 */
package com.sodo.xmarketing.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * @author tahi1990
 */

public abstract class BaseEntity<T> implements Serializable {

  @Indexed(direction = IndexDirection.DESCENDING)
  protected boolean isDelete = false;

  @CreatedDate
  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @Indexed(direction = IndexDirection.DESCENDING)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @Indexed(direction = IndexDirection.DESCENDING)
  private LocalDateTime lastModifiedDate;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;

  // @Henry -> Thêm field này để phục vụ full Text Search
  @TextIndexed
  private String textSearch;

  public abstract T getId();

  public abstract void setId(T id);

  public String getTextSearch() {
    return textSearch;
  }

  public void setTextSearch(String textSearch) {
    this.textSearch = textSearch;
  }

  /**
   * @return the lastModifiedBy
   */
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  /**
   * @param lastModifiedBy the lastModifiedBy to set
   */
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /**
   * @return the createdBy
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * @param createdBy the createdBy to set
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * @return the isDelete
   */
  public boolean getIsDelete() {
    return isDelete;
  }


  /**
   * @param isDelete the isDelete to set
   */
  public void setIsDelete(boolean isDelete) {
    this.isDelete = isDelete;
  }


  /**
   * @return the createdDate
   */
  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the lastModifiedDate
   */
  public LocalDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  /**
   * @param lastModifiedDate the lastModifiedDate to set
   */
  public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

}
