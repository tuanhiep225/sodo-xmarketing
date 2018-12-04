package com.sodo.xmarketing.service;

import com.sodo.xmarketing.model.config.Domain;
import com.sodo.xmarketing.model.config.Format;
import java.util.List;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 10/38
 */
public interface DomainService {

  boolean removeAll();

  List<Domain> findAll();

  Domain findOneByDomainName(String domainName);

  List<Format> getFormatsOfCmsByDomainName(String domainName);

  void initDomain();
}
