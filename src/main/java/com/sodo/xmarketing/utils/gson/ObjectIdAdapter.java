package com.sodo.xmarketing.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bson.types.ObjectId;

/**
 * @author HenryDo
 * @created 29/11/2017 10:17 AM
 */
public class ObjectIdAdapter implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId> {

  @Override
  public JsonElement serialize(ObjectId objectId, Type typeOfSrc,
      JsonSerializationContext context) {
    return new JsonPrimitive(objectId.toHexString());
  }

  @Override
  public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return new ObjectId(json.getAsString());
  }
}
