package com.sodo.xmarketing.service.impl;

import com.sodo.xmarketing.model.config.Format;
import com.sodo.xmarketing.service.FormatService;
import com.sodo.xmarketing.utils.ConfigHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 10/01
 */
@Service
public class FormatServiceImpl implements FormatService {

  @Autowired
  private ConfigHelper configHelper;

  public void init() {
  }

  @Override
  public Format getEn() {
    Format formatConfigEn = new Format();

    formatConfigEn.setLang("en");
    formatConfigEn.setThousands(",");
    formatConfigEn.setDecimal(".");
    formatConfigEn.setDate("yyyy/mm/dd");
    formatConfigEn.setNumberInt("1.0-0");
    formatConfigEn.setNumberFloat("1.0-2");
    formatConfigEn.setCurrencyFormat("1.0-0");
    formatConfigEn.setCurrencyName("USD");
    formatConfigEn.setCurrencyPrecision(0);
    formatConfigEn.setCountryCode("US");
    formatConfigEn.setCurrency("USD");

    return formatConfigEn;
  }

  @Override
  public void initEn() {

    configHelper.saveConfig(getEn(), "en", Format.class);
  }

  @Override
  public Format getTh() {
    Format formatConfigTh = new Format();

    formatConfigTh.setLang("th");
    formatConfigTh.setThousands(",");
    formatConfigTh.setDecimal(".");
    formatConfigTh.setDate("yyyy/mm/dd");
    formatConfigTh.setNumberInt("1.0-0");
    formatConfigTh.setNumberFloat("1.0-2");
    formatConfigTh.setCurrencyFormat("1.0-0");
    formatConfigTh.setCurrencyName("VND");
    formatConfigTh.setCurrencyPrecision(0);
    formatConfigTh.setCountryCode("TH");
    formatConfigTh.setCurrency("THB");

    return formatConfigTh;
  }

  @Override
  public void initTh() {
    configHelper.saveConfig(getTh(), "th", Format.class);
  }

  @Override
  public Format getVi() {
    Format formatConfigVi = new Format();

    formatConfigVi.setLang("vi");
    formatConfigVi.setThousands(".");
    formatConfigVi.setDecimal(",");
    formatConfigVi.setDate("dd/mm/yyyy");
    formatConfigVi.setNumberInt("1.0-0");
    formatConfigVi.setNumberFloat("1.0-2");
    formatConfigVi.setCurrencyFormat("1.0-0");
    formatConfigVi.setCurrencyName("VND");
    formatConfigVi.setCurrencyPrecision(0);
    formatConfigVi.setCountryCode("VN");
    formatConfigVi.setCurrency("VND");

    return formatConfigVi;
  }

  @Override
  public void initVi() {
    configHelper.saveConfig(getVi(), "vi", Format.class);
  }

  @Override
  public Format getCn() {
    Format formatConfigCN = new Format();

    formatConfigCN.setLang("zh");
    formatConfigCN.setThousands(",");
    formatConfigCN.setDecimal(".");
    formatConfigCN.setDate("yyyy/mm/dd");
    formatConfigCN.setNumberInt("1.0-0");
    formatConfigCN.setNumberFloat("1.0-2");
    formatConfigCN.setCurrencyFormat("1.0-0");
    formatConfigCN.setCurrencyName("VND");
    formatConfigCN.setCurrencyPrecision(0);
    formatConfigCN.setCountryCode("CN");
    formatConfigCN.setCurrency("CNY");

    return formatConfigCN;
  }

  @Override
  public void initCN() {
    configHelper.saveConfig(getCn(), "cn", Format.class);
  }
}
