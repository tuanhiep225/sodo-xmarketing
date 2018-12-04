package com.sodo.xmarketing.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.bson.types.ObjectId;

/**
 * Created by Henry Do User: henrydo Date: 21/06/2018 Time: 09/04
 */
public class GsonHelper {

  public static Gson getGson() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
    builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
    builder.registerTypeAdapter(ObjectId.class, new ObjectIdAdapter());
    builder.setDateFormat("dd/MM/yyyy HH:mm:ss");

    return builder.create();
  }
}
