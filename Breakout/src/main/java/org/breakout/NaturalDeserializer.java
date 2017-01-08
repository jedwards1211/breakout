package org.breakout;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class NaturalDeserializer implements JsonDeserializer<Object> {
	@Override
	public Object deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
		if (json.isJsonNull()) {
			return null;
		} else if (json.isJsonPrimitive()) {
			return handlePrimitive(json.getAsJsonPrimitive());
		} else if (json.isJsonArray()) {
			return handleArray(json.getAsJsonArray(), context);
		} else {
			return handleObject(json.getAsJsonObject(), context);
		}
	}

	private Object handlePrimitive(JsonPrimitive json) {
		if (json.isBoolean()) {
			return json.getAsBoolean();
		} else if (json.isString()) {
			return json.getAsString();
		} else {
			BigDecimal bigDec = json.getAsBigDecimal();
			// Find out if it is an int type
			try {
				bigDec.toBigIntegerExact();
				try {
					return bigDec.intValueExact();
				} catch (ArithmeticException e) {
				}
				return bigDec.longValue();
			} catch (ArithmeticException e) {
			}
			// Just return it as a double
			return bigDec.doubleValue();
		}
	}

	private Object handleArray(JsonArray json, JsonDeserializationContext context) {
		List<?> list = new ArrayList<>(json.size());
		for (int i = 0; i < json.size(); i++) {
			list.add(context.deserialize(json.get(i), Object.class));
		}
		return list;
	}

	private Object handleObject(JsonObject json, JsonDeserializationContext context) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			map.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));
		}
		return map;
	}
}
