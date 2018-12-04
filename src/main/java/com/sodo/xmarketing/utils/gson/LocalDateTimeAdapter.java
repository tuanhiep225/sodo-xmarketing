package com.sodo.xmarketing.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author HenryDo
 * @created 29/11/2017 10:17 AM
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>,
    JsonDeserializer<LocalDateTime> {

  @Override
  public JsonElement serialize(LocalDateTime date, Type typeOfSrc,
      JsonSerializationContext context) {
    return new JsonPrimitive(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
  }

  @Override
  public LocalDateTime deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context)
      throws JsonParseException {
    return LocalDateTime
        .parse(json.getAsString(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
  }
}
