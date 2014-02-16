package org.andork.gson2snakeyaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Gson2SnakeYaml {
	private Gson2SnakeYaml() {
		
	}
	
	public static Object toSnakeYaml(JsonElement elem) {
		if (elem instanceof JsonPrimitive) {
			return ((JsonPrimitive) elem).getAsString();
		}
		else if (elem instanceof JsonObject) {
			return toSnakeYamlMap((JsonObject) elem);
		}
		else if (elem instanceof JsonArray) {
			return toSnakeYamlList((JsonArray) elem);
		}
		else {
			return null;
		}
	}
	
	public static Map<String, ?> toSnakeYamlMap(JsonObject obj) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			result.put(entry.getKey(), toSnakeYaml(entry.getValue()));
		}
		return result;
	}
	
	public static List<?> toSnakeYamlList(JsonArray array) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			result.add(toSnakeYaml(array.get(i)));
		}
		return result;
	}
}
