package org.breakout;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NaturalSerializer implements JsonSerializer<Object> {
	@SuppressWarnings("unchecked")
	@Override
	public JsonElement serialize(Object obj, Type typeOfT,
			JsonSerializationContext context) {
		if (obj == null) {
			return JsonNull.INSTANCE;
		} else if (obj instanceof List) {
			return handleList((List<?>) obj, context);
		} else if (obj instanceof Map) {
			return handleMap((Map<String, ?>) obj, context);
		} else {
			return handlePrimitive(obj);
		}
	}

	private JsonPrimitive handlePrimitive(Object prim) {
		if (prim instanceof Boolean) {
			return new JsonPrimitive((boolean) prim);
		} else if (prim instanceof Number) {
			return new JsonPrimitive((Number) prim);
		} else {
			return new JsonPrimitive(prim.toString());
		}
	}

	private JsonArray handleList(List<?> list, JsonSerializationContext context) {
		JsonArray array = new JsonArray();
		for (Object elem : list) {
			array.add(context.serialize(elem));
		}
		return array;
	}

	private JsonObject handleMap(Map<String, ?> map, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			object.add(entry.getKey(), context.serialize(entry.getValue()));
		}
		return object;
	}
}
