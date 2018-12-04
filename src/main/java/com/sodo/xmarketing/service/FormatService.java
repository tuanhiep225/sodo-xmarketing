package com.sodo.xmarketing.service;

import com.sodo.xmarketing.model.config.Format;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 10/01
 */
public interface FormatService {

  Format getEn();

  void initEn();

  Format getTh();

  void initTh();

  Format getVi();

  void initVi();

  Format getCn();

  void initCN();
}
