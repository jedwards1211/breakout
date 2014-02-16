package org.andork.gson2snakeyaml;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SnakeYaml2Gson {
	private SnakeYaml2Gson() {

	}

	public static JsonElement toJsonElement(Object yaml) {
		if (yaml == null) {
			return JsonNull.INSTANCE;
		}
		else if (yaml instanceof List) {
			return toJsonArray((List<?>) yaml);
		}
		else if (yaml instanceof Map) {
			return toJsonObject((Map<?, ?>) yaml);
		}
		else {
			return new JsonPrimitive(yaml.toString());
		}
	}

	public static JsonArray toJsonArray(List<?> yaml) {
		JsonArray array = new JsonArray();
		for (Object o : yaml) {
			array.add(toJsonElement(o));
		}
		return array;
	}

	public static JsonObject toJsonObject(Map<?, ?> yaml) {
		JsonObject obj = new JsonObject();
		for (Map.Entry<?, ?> entry : yaml.entrySet()) {
			obj.add(entry.getKey().toString(), toJsonElement(entry.getValue()));
		}
		return obj;
	}
}
