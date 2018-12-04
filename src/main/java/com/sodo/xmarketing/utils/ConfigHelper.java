package com.sodo.xmarketing.utils;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sodo.xmarketing.model.config.Config;
import com.sodo.xmarketing.repository.config.ConfigRepository;
import com.sodo.xmarketing.utils.gson.GsonHelper;
import com.sodo.xmarketing.utils.gson.LocalDateAdapter;
import com.sodo.xmarketing.utils.gson.LocalDateTimeAdapter;
import com.sodo.xmarketing.utils.gson.ObjectIdAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigHelper {

  @Autowired
  private ConfigRepository repository;

  public ConfigHelper() {
  }

  public <E> E getConfig(String groupName, Class<E> configClass) {
    Config config = repository.findByKey(getKey(groupName, configClass).toLowerCase());

    if (config == null) {
      return null;
    }

    return parserConfig(config, configClass);
  }

  public <E> E getConfig(Class<E> configClass) {
    return getConfig(null, configClass);
  }

  public <E> E saveConfig(E config, String groupName, Class<E> configClass) {
    if (config == null) {
      return null;
    }

    Config configDb = new Config();

    configDb.setValue(GsonHelper.getGson().toJson(config));

    configDb.setKey(getKey(groupName, configClass).toLowerCase());

    Config configDbAfter = repository.updateConfig(configDb);

    if (configDbAfter == null) {

      return null;
    }

    return parserConfig(configDbAfter, configClass);
  }

  public <E> E saveConfig(E config, Class<E> configClass) {
    return saveConfig(config, null, configClass);
  }

  private <E> String getKey(String groupName, Class<E> configClass) {
    String key = configClass.getName();

    if (!isNullOrEmpty(groupName)) {
      key = groupName + "." + key;
    }
    return key;
  }

  private <E> E parserConfig(Config config, Class<E> configClass) {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
    builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
    builder.registerTypeAdapter(ObjectId.class, new ObjectIdAdapter());
    builder.setDateFormat("dd/MM/yyyy HH:mm:ss");

    Gson gson = builder.create();

    return gson.fromJson(config.getValue(), configClass);
  }

  public <E> List<E> getListConfigByConfigClass(Class<E> configClass) {

    List<Config> configList = repository.findByKeyClass(configClass.getName().replace('$', '.'));

    List<E> result = new ArrayList<>();
    if (configList != null) {
      configList.forEach(config -> result.add(parserConfig(config, configClass)));
    }
    return result;
  }

  public <E> E saveDuplicateConfig(E config, String groupName, Class<E> configClass) {
    if (config == null) {
      return null;
    }

    Config configDb = new Config();

    configDb.setValue(GsonHelper.getGson().toJson(config));

    configDb.setKey(getKey(groupName, configClass).toLowerCase());

    Config configDbAfter = repository.save(configDb);

    if (configDbAfter == null) {
      return null;
    }

    return parserConfig(configDbAfter, configClass);
  }
}
