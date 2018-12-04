package com.sodo.xmarketing.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;

import com.sodo.xmarketing.model.entity.BaseEntity;

/**
 * @author tahi1990
 */

@NoRepositoryBean
public interface BaseRepository<TE extends BaseEntity<T>, T extends Serializable>
extends ReadOnlyRepository<TE, T> {

  /**
   *
   * @param entity
   * @return
   */
  default TE add(TE entity) {
    return save(entity);
  }

  /**
   *
   * @param id
   */
  default boolean remove(T id) {
    findById(id).ifPresent(e -> {
      e.setIsDelete(true);
      save(e);
    });

    return true;
  }

  /**
   *
   * @param entity
   */
  default TE update(TE entity) {

    if (entity.getId() == null) {
      return null;
    }

    return save(entity);
  }

}
