package com.sodo.xmarketing.repository.config;

import com.sodo.xmarketing.model.config.Config;

/**
 * @author HenryDo
 * @created 27/10/2017 9:37 AM
 */
public interface ConfigCustomRepository {

  Config updateConfig(Config config);
}