/**
 *
 */
package com.sodo.xmarketing.repository;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.sodo.xmarketing.model.entity.BaseEntity;

/**
 * @author tahi1990
 */
@NoRepositoryBean
public interface ReadOnlyRepository<TE extends BaseEntity<T>, T extends Serializable>
extends MongoRepository<TE, T> {

  /**
   *
   * @param id
   * @return
   */
  default boolean contains(T id) {
    return existsById(id);
  }

  /**
   *
   * @param id
   * @return
   */
  @Query("{'id':'?0','isDelete':false}")
  TE get(T id);

  @Query("{'isDelete':false}")
  Collection<TE> getAll();

  @Query("{'isDelete':false}")
  Page<TE> getAll(Pageable pageable);

}
