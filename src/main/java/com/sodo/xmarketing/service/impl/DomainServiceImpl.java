package com.sodo.xmarketing.service.impl;

import com.sodo.xmarketing.model.common.CommonStatic;
import com.sodo.xmarketing.model.config.Domain;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.repository.config.DomainRepository;
import com.sodo.xmarketing.service.DomainService;
import com.sodo.xmarketing.service.FormatService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 10/38
 */
@Service
public class DomainServiceImpl implements DomainService {

  private static final String CUSTOMER = CommonStatic.CUSTOMER;
  private static final String CMS = CommonStatic.CMS;

  @Autowired
  private FormatService formatService;

  @Autowired
  private DomainRepository repository;

  /**
   * @Henry xóa tất cả các domain
   */
  @Override
  public boolean removeAll() {
    return repository.removeAllByIsDeleteFalse();
  }

  /**
   * @Henry Lấy ra toàn bộ các domain
   */
  @Override
  public List<Domain> findAll() {
    return repository.findAllByIsDeleteIsFalse();
  }

  /**
   * @param domainName tên domain cần lấy ra
   * @return Domain tìm thấy
   * @Henry Lấy ra thông số của domain
   */
  @Override
  public Domain findOneByDomainName(String domainName) {
    return repository.findFirstByIsDeleteIsFalseAndName(domainName);
  }

  @Override
  public List<Format> getFormatsOfCmsByDomainName(String domainName) {
    Domain domain = repository.findFirstByDomainsContainingAndTypeAndIsDeleteIsFalse(domainName, CMS);

    if (domain == null) {
      return new ArrayList<>();
    }

    return domain.getLangs();
  }

  /**
   * @Henry Khởi tạo domain
   */
  @Override
  public void initDomain() {
    addThDomain();
    addVnDomain();
    addEnDomain();
    addCNDomain();

    // Thêm domain cms
    addCmsDomain();
  }

  private void addCmsDomain() {
    List<Format> formats = Arrays.asList(formatService.getVi(),
        formatService.getTh(),
        formatService.getEn(),
        formatService.getCn());

    List<String> domains = repository.findAllByIsDeleteIsFalseAndType(CUSTOMER).stream()
        .map(Domain::getName).collect(Collectors.toList());

    Domain domain = Domain.builder()
        .name("cms." + CommonStatic.DOMAIN)
        .type(CMS)
        .defaultLang("vi")
        .langs(formats)
        .domains(domains)
        .build();

    repository.add(domain);
  }

  private void addVnDomain() {
    List<Format> formats = Arrays.asList(formatService.getVi(),
        formatService.getTh(),
        formatService.getEn(),
        formatService.getCn());

    Domain domain = Domain.builder()
        .name("vi." + CommonStatic.DOMAIN)
        .codePrefix("VN")
        .type(CUSTOMER)
        .defaultLang("vi")
        .langs(formats)
        .build();

    repository.add(domain);
  }

  private void addThDomain() {
    Domain domain = Domain.builder()
        .name("th." + CommonStatic.DOMAIN)
        .codePrefix("TH")
        .type(CUSTOMER)
        .defaultLang("th")
        .langs(Collections.singletonList(formatService.getTh()))
        .build();

    repository.add(domain);
  }

  private void addEnDomain() {
    Domain domain = Domain.builder()
        .name("en." + CommonStatic.DOMAIN)
        .codePrefix("EN")
        .type(CUSTOMER)
        .defaultLang("en")
        .langs(Collections.singletonList(formatService.getEn()))
        .build();

    repository.add(domain);
  }

  private void addCNDomain() {
    Domain domain = Domain.builder()
        .name("cn." + CommonStatic.DOMAIN)
        .codePrefix("CN")
        .type(CUSTOMER)
        .defaultLang("zh")
        .langs(Collections.singletonList(formatService.getCn()))
        .build();

    repository.add(domain);
  }
}
