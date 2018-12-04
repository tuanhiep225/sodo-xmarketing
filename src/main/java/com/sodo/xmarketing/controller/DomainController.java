package com.sodo.xmarketing.controller;

import com.sodo.xmarketing.model.config.Domain;
import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.service.DomainService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 11/16
 */
@RestController
@RequestMapping("/api/v1/domains")
public class DomainController {


  private final DomainService domainService;

  @Autowired
  public DomainController(DomainService domainService) {
    this.domainService = domainService;
  }

  /**
   * @Henry Khởi tạo domain
   */
  @GetMapping("/initialization")
  public List<Domain> initialization() {
    domainService.initDomain();
    return domainService.findAll();
  }

  /**
   * @Henry Lấy thông số cấu hình theo domain
   */
  @GetMapping("/{domainName}")
  public Domain findByDomainName(@PathVariable String domainName) {
    return domainService.findOneByDomainName(domainName);
  }

  /**
   * @Henry Lấy ra các thông số format tiền tệ của cms.
   */
  @GetMapping("/cms/domains/{domainName}")
  public List<Format> getFormatsOfCmsByDomainName(@PathVariable String domainName) {
    return domainService.getFormatsOfCmsByDomainName(domainName);
  }
}
