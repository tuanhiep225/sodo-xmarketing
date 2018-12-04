package com.sodo.xmarketing.repository.config;

import com.sodo.xmarketing.model.config.Domain;
import com.sodo.xmarketing.repository.BaseRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 11/12
 */
@Repository
public interface DomainRepository extends BaseRepository<Domain, String> {

  List<Domain> findAllByIsDeleteIsFalse();

  List<Domain> findAllByIsDeleteIsFalseAndType(String type);

  Domain findFirstByDomainsContainingAndTypeAndIsDeleteIsFalse(String domainName, String type);

  Domain findFirstByIsDeleteIsFalseAndName(String domainName);

  boolean removeAllByIsDeleteFalse();
}
